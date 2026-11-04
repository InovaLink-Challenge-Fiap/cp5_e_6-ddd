package br.com.fiap.bank.plataforma.infrastructure.persistencia;

import br.com.fiap.bank.plataforma.domain.model.Cpf;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.repository.SolicitacaoCreditoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Implementa a porta de solicitacoes com JPA, convertendo entre agregado e entidade
@Repository
@RequiredArgsConstructor
public class SolicitacaoCreditoRepositoryImpl implements SolicitacaoCreditoRepository {

    private final SolicitacaoCreditoJpaRepository repositorioJpa;

    // Grava a entidade e devolve o agregado com o id gerado pelo banco
    @Override
    public SolicitacaoCredito salvar(SolicitacaoCredito solicitacao) {
        return paraDominio(repositorioJpa.save(paraEntidade(solicitacao)));
    }

    // Busca pelo id e converte para o agregado
    @Override
    public Optional<SolicitacaoCredito> buscarPorId(Long id) {
        return repositorioJpa.findById(id).map(this::paraDominio);
    }

    // Solicitacoes do CPF, mais recentes primeiro
    @Override
    public List<SolicitacaoCredito> listarPorCpf(Cpf cpf) {
        return repositorioJpa.findByCpfClienteOrderByDataSolicitacaoDesc(cpf.valor()).stream()
                .map(this::paraDominio)
                .toList();
    }

    // Todas as solicitacoes, mais recentes primeiro
    @Override
    public List<SolicitacaoCredito> listarTodas() {
        return repositorioJpa.findAllByOrderByDataSolicitacaoDesc().stream()
                .map(this::paraDominio)
                .toList();
    }

    // Copia os dados do agregado para a entidade JPA, com o CPF so em digitos
    private SolicitacaoCreditoEntity paraEntidade(SolicitacaoCredito solicitacao) {
        return new SolicitacaoCreditoEntity(
                solicitacao.getId(),
                solicitacao.getCpfCliente().valor(),
                solicitacao.getNomeCliente(),
                solicitacao.getTipoCredito(),
                solicitacao.getValorSolicitado(),
                solicitacao.getQuantidadeParcelas(),
                solicitacao.getRendaMensal(),
                solicitacao.getScoreCredito(),
                solicitacao.getStatus(),
                solicitacao.getParecerOperador(),
                solicitacao.getScrRestricoes(),
                solicitacao.getScoreBureau(),
                solicitacao.getDataSolicitacao(),
                solicitacao.getDataAtualizacao()
        );
    }

    // Recria o agregado a partir da entidade JPA
    private SolicitacaoCredito paraDominio(SolicitacaoCreditoEntity entidade) {
        return SolicitacaoCredito.reconstituir(
                entidade.getId(),
                new Cpf(entidade.getCpfCliente()),
                entidade.getNomeCliente(),
                entidade.getTipoCredito(),
                entidade.getValorSolicitado(),
                entidade.getQuantidadeParcelas(),
                entidade.getRendaMensal(),
                entidade.getScoreCredito(),
                entidade.getStatus(),
                entidade.getParecerOperador(),
                entidade.getScrRestricoes(),
                entidade.getScoreBureau(),
                entidade.getDataSolicitacao(),
                entidade.getDataAtualizacao()
        );
    }
}
