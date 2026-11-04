package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.dto.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.excecao.DadosInvalidosException;
import br.com.fiap.bank.plataforma.application.excecao.RecursoNaoEncontradoException;
import br.com.fiap.bank.plataforma.domain.excecao.TransicaoDeStatusInvalidaException;
import br.com.fiap.bank.plataforma.domain.integracao.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.integracao.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.integracao.CrmGateway;
import br.com.fiap.bank.plataforma.domain.integracao.NotificacaoGateway;
import br.com.fiap.bank.plataforma.domain.integracao.ScrGateway;
import br.com.fiap.bank.plataforma.domain.model.ContaCliente;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import br.com.fiap.bank.plataforma.domain.model.ResultadoAnalise;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.model.StatusSolicitacao;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import br.com.fiap.bank.plataforma.domain.service.MotorDeCredito;

import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Casos de uso das solicitacoes, usados pela API v1, pelo adaptador legado e pela carga inicial
@Service
@RequiredArgsConstructor
public class SolicitacaoCreditoService {

    private final SolicitacaoCreditoRepository solicitacaoRepository;
    private final ScrGateway scrGateway;
    private final BureauCreditoGateway bureauGateway;
    private final CrmGateway crmGateway;
    private final CoreBancarioGateway coreBancarioGateway;
    private final NotificacaoGateway notificacaoGateway;
    private final MotorDeCredito motorDeCredito;

    // Analisa com SCR, Bureau, CRM e Core, grava e avisa o cliente; sem transacao por causa do Core
    public SolicitacaoResponse solicitar(SolicitarCreditoRequest request) {
        Cpf cpf = new Cpf(request.cpfCliente());

        boolean restricaoScr = scrGateway.possuiRestricoes(cpf);
        int scoreBureau = bureauGateway.consultarScore(cpf);
        boolean inadimplenciaCrm = crmGateway.possuiInadimplencia(cpf);
        ContaCliente conta = coreBancarioGateway.consultarConta(cpf);
        conta.exigirAtiva();

        ResultadoAnalise resultado = motorDeCredito.analisar(restricaoScr, scoreBureau, inadimplenciaCrm,
                request.valorSolicitado(), request.quantidadeParcelas(), request.rendaMensal());

        SolicitacaoCredito salva = solicitacaoRepository.salvar(SolicitacaoCredito.nova(
                cpf, request.nomeCliente(), request.tipoCredito(), request.valorSolicitado(),
                request.quantidadeParcelas(), request.rendaMensal(), restricaoScr, scoreBureau, resultado));

        notificacaoGateway.notificar(cpf, "Sua solicitação de crédito #" + salva.getId()
                + " está no status: " + salva.getStatus());

        return SolicitacaoResponse.de(salva);
    }

    // Solicitacoes de um CPF, mais recentes primeiro
    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> consultarPorCpf(String cpf) {
        return solicitacaoRepository.listarPorCpf(new Cpf(cpf)).stream()
                .map(SolicitacaoResponse::de)
                .toList();
    }

    // Busca uma solicitacao pelo id; lanca RecursoNaoEncontradoException se nao existir
    @Transactional(readOnly = true)
    public SolicitacaoResponse buscarPorId(Long id) {
        return SolicitacaoResponse.de(buscarSolicitacao(id));
    }

    // Todas as solicitacoes, mais recentes primeiro
    @Transactional(readOnly = true)
    public List<SolicitacaoResponse> listarTodas() {
        return solicitacaoRepository.listarTodas().stream()
                .map(SolicitacaoResponse::de)
                .toList();
    }

    // Aprova ou reprova pela Mesa e avisa o cliente
    @Transactional
    public SolicitacaoResponse atualizarStatus(Long id, String status, String parecer) {
        StatusSolicitacao destino = converterStatus(status);
        SolicitacaoCredito solicitacao = buscarSolicitacao(id);

        String mensagem = switch (destino) {
            case APROVADO -> {
                solicitacao.aprovar(parecer);
                yield "PARABÉNS! Sua solicitação de crédito #" + id + " foi APROVADA pela Mesa de Operações.";
            }
            case REPROVADO -> {
                solicitacao.reprovar(parecer);
                yield "Sua solicitação de crédito #" + id + " foi REPROVADA pela Mesa de Operações.";
            }
            default -> throw new TransicaoDeStatusInvalidaException(solicitacao.getStatus(), destino);
        };

        SolicitacaoCredito salva = solicitacaoRepository.salvar(solicitacao);
        notificacaoGateway.notificar(salva.getCpfCliente(), mensagem);
        return SolicitacaoResponse.de(salva);
    }

    // Consulta a conta do cliente no Core Bancario
    public ContaClienteResponse consultarContaCliente(String cpf) {
        return ContaClienteResponse.de(coreBancarioGateway.consultarConta(new Cpf(cpf)));
    }

    // Carrega o agregado ou lanca RecursoNaoEncontradoException
    private SolicitacaoCredito buscarSolicitacao(Long id) {
        return solicitacaoRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Solicitacao nao encontrada com ID: " + id));
    }

    // Converte o texto no StatusSolicitacao de mesmo nome; texto nulo ou desconhecido e dado invalido
    private StatusSolicitacao converterStatus(String status) {
        return Arrays.stream(StatusSolicitacao.values())
                .filter(valor -> valor.name().equals(status))
                .findFirst()
                .orElseThrow(() -> new DadosInvalidosException("Status deve ser APROVADO ou REPROVADO"));
    }
}
