package br.com.fiap.bank.core.infrastructure.persistencia;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Repositorio Spring Data da tabela tb_cliente; a conta vem junto pelo relacionamento conta_id
public interface ClienteJpaRepository extends JpaRepository<ClienteEntity, UUID> {

    // Busca os clientes ja com a conta
    @Override
    @EntityGraph(attributePaths = "conta")
    List<ClienteEntity> findAll(Sort sort);

    // Cliente pelo CPF so com digitos
    Optional<ClienteEntity> findByCpf(String cpf);

    // Consulta de CPF duplicado sem carregar o cliente
    boolean existsByCpf(String cpf);

    // Cliente dono da conta com esse numero no formato NNNNN-D
    Optional<ClienteEntity> findByContaNumeroConta(String numeroConta);

    // Usado ao gerar numero de conta para nao repetir
    boolean existsByContaNumeroConta(String numeroConta);

    // So os clientes com conta, em ordem de numero da conta e com a conta no mesmo select
    @Query("select c from ClienteEntity c join fetch c.conta conta order by conta.numeroConta")
    List<ClienteEntity> findAllComConta();
}
