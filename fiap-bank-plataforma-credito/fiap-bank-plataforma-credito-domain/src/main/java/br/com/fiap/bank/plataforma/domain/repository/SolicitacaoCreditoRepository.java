package br.com.fiap.bank.plataforma.domain.repository;

import br.com.fiap.bank.plataforma.domain.model.Cpf;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;

import java.util.List;
import java.util.Optional;

// Porta de persistencia das solicitacoes de credito
public interface SolicitacaoCreditoRepository {

    // Grava a solicitacao e devolve a versao salva, ja com id
    SolicitacaoCredito salvar(SolicitacaoCredito solicitacao);

    // Busca a solicitacao pelo id
    Optional<SolicitacaoCredito> buscarPorId(Long id);

    // Solicitacoes do CPF, da mais recente para a mais antiga
    List<SolicitacaoCredito> listarPorCpf(Cpf cpf);

    // Todas as solicitacoes, da mais recente para a mais antiga
    List<SolicitacaoCredito> listarTodas();
}
