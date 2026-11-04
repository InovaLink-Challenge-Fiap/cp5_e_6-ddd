package br.com.fiap.bank.core.infrastructure.persistencia;

import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Adaptador que implementa o ClienteRepository do dominio em cima do Spring Data JPA
@Repository
@RequiredArgsConstructor
public class ClienteRepositoryImpl implements ClienteRepository {

    private final ClienteJpaRepository repositorioJpa;

    // Converte para entidade, grava o cliente e a conta em cascata e devolve como objeto de dominio
    @Override
    public Cliente salvar(Cliente cliente) {
        return MapeadorPersistencia.paraDominio(repositorioJpa.save(MapeadorPersistencia.paraEntidade(cliente)));
    }

    // Procura pelo id e converte o resultado para o dominio
    @Override
    public Optional<Cliente> buscarPorId(UUID id) {
        return repositorioJpa.findById(id).map(MapeadorPersistencia::paraDominio);
    }

    // Procura pelos digitos do CPF e converte o resultado para o dominio
    @Override
    public Optional<Cliente> buscarPorCpf(Cpf cpf) {
        return repositorioJpa.findByCpf(cpf.valor()).map(MapeadorPersistencia::paraDominio);
    }

    // Procura o dono da conta pelo numero e converte o resultado para o dominio
    @Override
    public Optional<Cliente> buscarPorNumeroConta(String numeroConta) {
        return repositorioJpa.findByContaNumeroConta(numeroConta).map(MapeadorPersistencia::paraDominio);
    }

    // Lista em ordem alfabetica de nome
    @Override
    public List<Cliente> listarTodos() {
        return repositorioJpa.findAll(Sort.by("nome")).stream().map(MapeadorPersistencia::paraDominio).toList();
    }

    // Lista so quem tem conta, em ordem de numero da conta
    @Override
    public List<Cliente> listarComConta() {
        return repositorioJpa.findAllComConta().stream().map(MapeadorPersistencia::paraDominio).toList();
    }

    // Consulta direto no banco se o CPF ja esta cadastrado
    @Override
    public boolean existePorCpf(Cpf cpf) {
        return repositorioJpa.existsByCpf(cpf.valor());
    }

    // Consulta direto no banco se o numero de conta ja foi usado
    @Override
    public boolean existePorNumeroConta(String numeroConta) {
        return repositorioJpa.existsByContaNumeroConta(numeroConta);
    }

    // Apaga a linha do cliente; a conta vai junto pelo cascade do relacionamento
    @Override
    public void remover(Cliente cliente) {
        repositorioJpa.deleteById(cliente.getId());
    }
}
