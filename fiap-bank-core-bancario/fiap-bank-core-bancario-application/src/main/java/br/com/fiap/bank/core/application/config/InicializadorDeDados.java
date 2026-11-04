package br.com.fiap.bank.core.application.config;

import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.entidade.ContaBancaria;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;
import br.com.fiap.bank.core.domain.objetovalor.Email;
import br.com.fiap.bank.core.domain.objetovalor.TipoConta;
import br.com.fiap.bank.core.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

// Carga inicial com 4 clientes e uma conta corrente para cada, feita ao subir a aplicacao
@Slf4j
@Component
@RequiredArgsConstructor
public class InicializadorDeDados implements CommandLineRunner {

    // Agencia das contas da carga inicial
    private static final String AGENCIA = "0001";

    private final ClienteRepository clienteRepository;

    // So carrega os dados se ainda nao existir nenhum cliente no banco
    @Override
    public void run(String... args) {
        if (!clienteRepository.listarTodos().isEmpty()) {
            return;
        }

        cadastrar("12345678900", "Ana Silva", "ana.silva@email.com", LocalDate.of(1990, 5, 10), "7500.00", "12345-6", "12500.00");
        cadastrar("98765432100", "Carlos Eduardo Oliveira", "carlos.oliveira@email.com", LocalDate.of(1985, 8, 22), "12000.00", "78901-2", "45800.50");
        cadastrar("55544433399", "Mariana Souza", "mariana.souza@email.com", LocalDate.of(1995, 1, 15), "4200.00", "33333-3", "800.00");
        cadastrar("11122233347", "Bruno Ferreira", "bruno.ferreira@email.com", LocalDate.of(1992, 3, 8), "5000.00", "44444-7", "3200.00");

        log.info("Core Bancario: dados iniciais carregados");
    }

    // Cria o cliente, abre a conta corrente dele com numero e saldo fixos e grava os dois juntos
    private void cadastrar(String cpf, String nome, String email, LocalDate dataNascimento, String rendaMensal, String numeroConta, String saldo) {
        Cliente cliente = Cliente.novo(new Cpf(cpf), nome, new Email(email), dataNascimento, new BigDecimal(rendaMensal));
        cliente.abrirConta(ContaBancaria.abrir(numeroConta, AGENCIA, TipoConta.CORRENTE, new BigDecimal(saldo)));
        clienteRepository.salvar(cliente);
    }
}
