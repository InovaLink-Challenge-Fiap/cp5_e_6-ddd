package br.com.fiap.bank.core.application.service;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponse;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.excecao.DadosInvalidosException;
import br.com.fiap.bank.core.application.excecao.RecursoNaoEncontradoException;
import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.entidade.ContaBancaria;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;
import br.com.fiap.bank.core.domain.objetovalor.OperacaoSaldo;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

// Casos de uso de conta: abertura, consulta, saldo e encerramento, sempre pelo cliente dono
@Service
@Transactional
@RequiredArgsConstructor
public class ContaBancariaService {

    // Agencia usada quando a requisicao de abertura nao informa uma
    private static final String AGENCIA_PADRAO = "0001";

    // Formato do numero de conta: 5 digitos, hifen e 1 digito
    private static final Pattern FORMATO_NUMERO_CONTA = Pattern.compile("\\d{5}-\\d");

    private final ClienteRepository clienteRepository;

    // Todas as contas em ordem de numero
    @Transactional(readOnly = true)
    public List<ContaResponse> listar() {
        return clienteRepository.listarComConta().stream().map(ContaResponse::de).toList();
    }

    // Conta pelo numero; lanca DadosInvalidosException ou RecursoNaoEncontradoException
    @Transactional(readOnly = true)
    public ContaResponse buscarPorNumero(String numeroConta) {
        return ContaResponse.de(obterDonoDaConta(numeroConta));
    }

    // Valida o CPF e devolve a conta do cliente; lanca RecursoNaoEncontradoException se nao houver
    @Transactional(readOnly = true)
    public ContaResponse buscarPorCpfCliente(String cpf) {
        Cpf cpfCliente = new Cpf(cpf);
        return clienteRepository.buscarPorCpf(cpfCliente)
                .filter(Cliente::possuiConta)
                .map(ContaResponse::de)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta nao encontrada: " + cpfCliente.valor()));
    }

    // Abre a conta de um cliente cadastrado
    public ContaResponse criar(CriarContaRequest request) {
        Cpf cpf = new Cpf(request.cpfCliente());
        Cliente cliente = clienteRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> new DadosInvalidosException("Nao existe cliente cadastrado com o CPF " + cpf.valor()));
        String agencia = request.agencia() != null ? request.agencia() : AGENCIA_PADRAO;
        cliente.abrirConta(ContaBancaria.abrir(gerarNumeroConta(), agencia, request.tipoConta(), request.saldoInicial()));
        return ContaResponse.de(clienteRepository.salvar(cliente));
    }

    // Aplica DEPOSITO ou SAQUE no saldo; as regras de movimentacao ficam na ContaBancaria
    public ContaResponse atualizarSaldo(String numeroConta, AtualizarSaldoRequest request) {
        OperacaoSaldo operacao = OperacaoSaldo.de(request.operacao());
        Cliente cliente = obterDonoDaConta(numeroConta);
        switch (operacao) {
            case DEPOSITO -> cliente.getConta().depositar(request.valor());
            case SAQUE -> cliente.getConta().sacar(request.valor());
        }
        return ContaResponse.de(clienteRepository.salvar(cliente));
    }

    // Encerra a conta sem apagar o registro
    public void encerrar(String numeroConta) {
        Cliente cliente = obterDonoDaConta(numeroConta);
        cliente.getConta().encerrar();
        clienteRepository.salvar(cliente);
    }

    // Valida o formato NNNNN-D e carrega o dono da conta ou lanca RecursoNaoEncontradoException
    private Cliente obterDonoDaConta(String numeroConta) {
        if (!FORMATO_NUMERO_CONTA.matcher(numeroConta).matches()) {
            throw new DadosInvalidosException("Numero de conta invalido: " + numeroConta);
        }
        return clienteRepository.buscarPorNumeroConta(numeroConta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta nao encontrada: " + numeroConta));
    }

    // Gera numero no formato NNNNN-D, sorteando de novo se ja existir
    private String gerarNumeroConta() {
        String numero;
        do {
            ThreadLocalRandom aleatorio = ThreadLocalRandom.current();
            numero = String.format("%05d-%d", aleatorio.nextInt(100_000), aleatorio.nextInt(10));
        } while (clienteRepository.existePorNumeroConta(numero));
        return numero;
    }
}
