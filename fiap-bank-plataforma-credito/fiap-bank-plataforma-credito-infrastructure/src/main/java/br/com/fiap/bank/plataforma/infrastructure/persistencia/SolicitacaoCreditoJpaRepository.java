package br.com.fiap.bank.plataforma.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repositorio Spring Data da tabela de solicitacoes
public interface SolicitacaoCreditoJpaRepository extends JpaRepository<SolicitacaoCreditoEntity, Long> {

    // Solicitacoes de um CPF, da mais recente para a mais antiga
    List<SolicitacaoCreditoEntity> findByCpfClienteOrderByDataSolicitacaoDesc(String cpfCliente);

    // Todas as solicitacoes, da mais recente para a mais antiga
    List<SolicitacaoCreditoEntity> findAllByOrderByDataSolicitacaoDesc();
}
