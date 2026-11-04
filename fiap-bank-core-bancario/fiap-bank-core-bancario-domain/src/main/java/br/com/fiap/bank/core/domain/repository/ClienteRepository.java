package br.com.fiap.bank.core.domain.repository;

import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Repositorio do agregado Cliente com a conta dele; o dominio define e a infrastructure implementa
public interface ClienteRepository {

    // Insere ou atualiza o cliente junto com a conta dele e devolve a versao gravada
    Cliente salvar(Cliente cliente);

    // Cliente com esse id, ou vazio se nao existir
    Optional<Cliente> buscarPorId(UUID id);

    // Cliente com esse CPF, ou vazio se nao existir
    Optional<Cliente> buscarPorCpf(Cpf cpf);

    // Cliente dono da conta com esse numero (formato NNNNN-D), ou vazio se nao existir
    Optional<Cliente> buscarPorNumeroConta(String numeroConta);

    // Todos os clientes cadastrados
    List<Cliente> listarTodos();

    // So os clientes que tem conta, em ordem de numero da conta
    List<Cliente> listarComConta();

    // Diz se o CPF ja esta em uso, para barrar cadastro duplicado
    boolean existePorCpf(Cpf cpf);

    // Diz se o numero de conta ja existe, para nao repetir ao gerar conta nova
    boolean existePorNumeroConta(String numeroConta);

    // Apaga o cliente e a conta dele
    void remover(Cliente cliente);
}
