package br.com.fiap.bank.plataforma.presentation.legado;

import br.com.fiap.bank.plataforma.application.dto.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import br.com.fiap.bank.plataforma.presentation.legado.dto.AprovarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.presentation.legado.dto.BuscarSolicitacaoRequest;
import br.com.fiap.bank.plataforma.presentation.legado.dto.ConsultarStatusRequest;
import br.com.fiap.bank.plataforma.presentation.legado.dto.ReprovarSolicitacaoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Adaptador das rotas antigas em /api/credito, que a Mesa de Operacoes e o app mobile ainda usam
@RestController
@RequestMapping("/api/credito")
@RequiredArgsConstructor
public class CreditoLegadoController {

    private final SolicitacaoCreditoService solicitacaoService;

    // Cria uma solicitacao do proprio cliente; responde 200 como a rota original
    @PostMapping("/solicitarCredito")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') and @acessoCliente.mesmoCpf(#request.cpfCliente())")
    public SolicitacaoResponse solicitarCredito(@Valid @RequestBody SolicitarCreditoRequest request) {
        return solicitacaoService.solicitar(request);
    }

    // Solicitacoes de um CPF; o cliente so consulta as proprias
    @PostMapping("/consultarStatusCredito")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR') or (hasAuthority('SCOPE_ROLE_CLIENTE') and @acessoCliente.mesmoCpf(#request.cpfCliente()))")
    public List<SolicitacaoResponse> consultarStatusCredito(@Valid @RequestBody ConsultarStatusRequest request) {
        return solicitacaoService.consultarPorCpf(request.cpfCliente());
    }

    // Busca uma solicitacao pelo id enviado no corpo; so operador e admin
    @PostMapping("/buscarSolicitacaoPorId")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoResponse buscarSolicitacaoPorId(@Valid @RequestBody BuscarSolicitacaoRequest request) {
        return solicitacaoService.buscarPorId(request.solicitacaoId());
    }

    // Lista todas as solicitacoes; so operador e admin
    @PostMapping("/listarTodasSolicitacoes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public List<SolicitacaoResponse> listarTodasSolicitacoes() {
        return solicitacaoService.listarTodas();
    }

    // Aprova uma solicitacao em analise com o parecer do operador
    @PostMapping("/aprovarSolicitacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoResponse aprovarSolicitacao(@Valid @RequestBody AprovarSolicitacaoRequest request) {
        return solicitacaoService.atualizarStatus(request.solicitacaoId(), "APROVADO", request.parecerOperador());
    }

    // Reprova uma solicitacao em analise com o motivo informado
    @PostMapping("/reprovarSolicitacao")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoResponse reprovarSolicitacao(@Valid @RequestBody ReprovarSolicitacaoRequest request) {
        return solicitacaoService.atualizarStatus(request.solicitacaoId(), "REPROVADO", request.motivoReprovacao());
    }

    // Conta do cliente no Core Bancario; o cliente so consulta a propria
    @PostMapping("/consultarClienteCore")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR') or (hasAuthority('SCOPE_ROLE_CLIENTE') and @acessoCliente.mesmoCpf(#request.cpfCliente()))")
    public ContaClienteResponse consultarClienteCore(@Valid @RequestBody ConsultarStatusRequest request) {
        return solicitacaoService.consultarContaCliente(request.cpfCliente());
    }
}
