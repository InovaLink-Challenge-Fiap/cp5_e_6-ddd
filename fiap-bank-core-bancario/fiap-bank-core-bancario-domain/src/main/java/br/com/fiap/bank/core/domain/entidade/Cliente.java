package br.com.fiap.bank.core.domain.entidade;

import br.com.fiap.bank.core.domain.excecao.ContaJaCadastradaException;
import br.com.fiap.bank.core.domain.excecao.OperacaoInvalidaException;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;
import br.com.fiap.bank.core.domain.objetovalor.Email;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

// Cliente do banco e raiz do agregado: dados cadastrais, renda e a conta dele
public final class Cliente {

    private final UUID id;
    private String nome;
    private Email email;
    private LocalDate dataNascimento;
    private Cpf cpf;
    private ContaBancaria conta;
    private BigDecimal rendaMensal;

    // Reconstroi um cliente ja existente com a conta dele, passando pelas validacoes do cadastro
    public Cliente(UUID id, Cpf cpf, String nome, Email email, LocalDate dataNascimento, BigDecimal rendaMensal, ContaBancaria conta) {
        this.id = id;
        this.conta = conta;
        atualizar(cpf, nome, email, dataNascimento, rendaMensal);
    }

    // Cria um cliente novo com id gerado e ainda sem conta
    public static Cliente novo(Cpf cpf, String nome, Email email, LocalDate dataNascimento, BigDecimal rendaMensal) {
        return new Cliente(UUID.randomUUID(), cpf, nome, email, dataNascimento, rendaMensal, null);
    }

    // Troca os dados cadastrais; CPF, email e nome obrigatorios, nascimento no passado e renda nao negativa
    public void atualizar(Cpf cpf, String nome, Email email, LocalDate dataNascimento, BigDecimal rendaMensal) {
        if (cpf == null || email == null) {
            throw new OperacaoInvalidaException("CPF e email do cliente sao obrigatorios");
        }
        if (nome == null || nome.isBlank()) {
            throw new OperacaoInvalidaException("Nome do cliente e obrigatorio");
        }
        if (dataNascimento == null || !dataNascimento.isBefore(LocalDate.now())) {
            throw new OperacaoInvalidaException("Data de nascimento deve estar no passado");
        }
        if (rendaMensal == null || rendaMensal.signum() < 0) {
            throw new OperacaoInvalidaException("Renda mensal nao pode ser negativa");
        }
        this.cpf = cpf;
        this.nome = nome.strip();
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.rendaMensal = rendaMensal.setScale(2, RoundingMode.HALF_EVEN);
    }

    // Liga a conta nova ao cliente; quem ja tem conta nao pode abrir outra
    public void abrirConta(ContaBancaria novaConta) {
        if (possuiConta()) {
            throw new ContaJaCadastradaException(cpf);
        }
        this.conta = novaConta;
    }

    // Diz se o cliente ja abriu a conta dele, mesmo que ela esteja encerrada
    public boolean possuiConta() {
        return conta != null;
    }

    public UUID getId() { return id; } // Identificador do cliente
    public String getNome() { return nome; } // Nome completo
    public Email getEmail() { return email; } // Email de contato
    public LocalDate getDataNascimento() { return dataNascimento; } // Data de nascimento
    public Cpf getCpf() { return cpf; } // CPF so com os digitos
    public ContaBancaria getConta() { return conta; } // Conta do cliente, ou null se ainda nao abriu
    public BigDecimal getRendaMensal() { return rendaMensal; } // Renda mensal com 2 casas

    // Dois clientes sao o mesmo quando tem o mesmo id
    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Cliente cliente)) {
            return false;
        }
        return Objects.equals(id, cliente.id);
    }

    // Hash calculado so pelo id, igual ao criterio do equals
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Texto com id e nome
    @Override
    public String toString() {
        return "Cliente[id=" + id + ", nome=" + nome + "]";
    }
}
