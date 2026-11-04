package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.ClienteResponse;
import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.application.excecao.ConflitoDeNegocioException;
import br.com.fiap.bank.core.application.excecao.RecursoNaoEncontradoException;
import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;
import br.com.fiap.bank.core.domain.objetovalor.Email;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

// Casos de uso de cliente: cadastro, consulta, atualizacao e remocao
@Service
@Transactional
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Todos os clientes em ordem de nome
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.listarTodos().stream().map(ClienteResponse::de).toList();
    }

    // Cliente pelo id; lanca RecursoNaoEncontradoException se nao existir
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(UUID id) {
        return ClienteResponse.de(obterCliente(id));
    }

    // Valida o CPF e devolve o cliente dono dele; lanca RecursoNaoEncontradoException se nao existir
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorCpf(String cpf) {
        Cpf cpfCliente = new Cpf(cpf);
        return clienteRepository.buscarPorCpf(cpfCliente)
                .map(ClienteResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado: " + cpfCliente.valor()));
    }

    // Cadastra cliente novo; CPF ja cadastrado e recusado
    public ClienteResponse criar(CriarClienteRequest request) {
        Cpf cpf = new Cpf(request.cpf());
        if (clienteRepository.existePorCpf(cpf)) {
            throw new ConflitoDeNegocioException("Ja existe cliente cadastrado com o CPF " + cpf.valor());
        }
        Cliente cliente = Cliente.novo(cpf, request.nome(), new Email(request.email()), request.dataNascimento(), request.rendaMensal());
        return ClienteResponse.de(clienteRepository.salvar(cliente));
    }

    // Substitui todos os dados do cliente; trocar de CPF so se o novo nao for de outro cliente
    public ClienteResponse atualizar(UUID id, CriarClienteRequest request) {
        Cliente cliente = obterCliente(id);
        Cpf cpf = new Cpf(request.cpf());
        if (!cpf.equals(cliente.getCpf()) && clienteRepository.existePorCpf(cpf)) {
            throw new ConflitoDeNegocioException("Ja existe cliente cadastrado com o CPF " + cpf.valor());
        }
        cliente.atualizar(cpf, request.nome(), new Email(request.email()), request.dataNascimento(), request.rendaMensal());
        return ClienteResponse.de(clienteRepository.salvar(cliente));
    }

    // Apaga o cliente e a conta dele; lanca RecursoNaoEncontradoException se nao existir
    public void remover(UUID id) {
        clienteRepository.remover(obterCliente(id));
    }

    // Carrega o cliente pelo id ou lanca RecursoNaoEncontradoException
    private Cliente obterCliente(UUID id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado: " + id));
    }
}
