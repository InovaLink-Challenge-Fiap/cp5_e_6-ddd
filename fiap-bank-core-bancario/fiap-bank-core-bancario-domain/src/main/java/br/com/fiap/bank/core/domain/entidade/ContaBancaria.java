package br.com.fiap.bank.core.domain.entidade;

import br.com.fiap.bank.core.domain.excecao.ContaEncerradaException;
import br.com.fiap.bank.core.domain.excecao.OperacaoInvalidaException;
import br.com.fiap.bank.core.domain.objetovalor.Saldo;
import br.com.fiap.bank.core.domain.objetovalor.StatusConta;
import br.com.fiap.bank.core.domain.objetovalor.TipoConta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

// Conta bancaria do cliente com numero, agencia, tipo, saldo, status e data de abertura
public final class ContaBancaria {

    private final UUID id;
    private final String numero;
    private final String agencia;
    private final TipoConta tipoConta;
    private Saldo saldo;
    private StatusConta statusConta;
    private final LocalDate dataAbertura;

    // Recria uma conta ja existente com todos os dados
    public ContaBancaria(UUID id, String numero, String agencia, TipoConta tipoConta, Saldo saldo, StatusConta statusConta, LocalDate dataAbertura) {
        this.id = id;
        this.numero = numero;
        this.agencia = agencia;
        this.tipoConta = tipoConta;
        this.saldo = saldo;
        this.statusConta = statusConta;
        this.dataAbertura = dataAbertura;
    }

    // Abre uma conta ATIVA com o saldo inicial e a data de hoje como abertura
    public static ContaBancaria abrir(String numero, String agencia, TipoConta tipoConta, BigDecimal saldoInicial) {
        return new ContaBancaria(UUID.randomUUID(), numero, agencia, tipoConta, new Saldo(saldoInicial), StatusConta.ATIVA, LocalDate.now());
    }

    // Deposito so em conta ativa e com valor maior que zero
    public void depositar(BigDecimal valor) {
        validarMovimentacao(valor);
        saldo = saldo.adicionar(valor);
    }

    // Saque so em conta ativa e com saldo suficiente
    public void sacar(BigDecimal valor) {
        validarMovimentacao(valor);
        saldo = saldo.subtrair(valor);
    }

    // Marca a conta como ENCERRADA; encerrar de novo nao muda nada
    public void encerrar() {
        statusConta = StatusConta.ENCERRADA;
    }

    // Barra movimentacao em conta encerrada ou bloqueada e valor zero, negativo ou nulo
    private void validarMovimentacao(BigDecimal valor) {
        if (statusConta == StatusConta.ENCERRADA) {
            throw new ContaEncerradaException(numero);
        }
        if (statusConta != StatusConta.ATIVA) {
            throw new OperacaoInvalidaException("Conta " + numero + " nao esta ativa");
        }
        if (valor == null || valor.signum() <= 0) {
            throw new OperacaoInvalidaException("Valor da operacao deve ser maior que zero");
        }
    }

    public UUID getId() { return id; } // Identificador da conta
    public String getNumero() { return numero; } // Numero no formato NNNNN-D
    public String getAgencia() { return agencia; } // Agencia com 4 digitos
    public TipoConta getTipoConta() { return tipoConta; } // CORRENTE, POUPANCA ou SALARIO
    public Saldo getSaldo() { return saldo; } // Saldo atual
    public StatusConta getStatusConta() { return statusConta; } // ATIVA, BLOQUEADA ou ENCERRADA
    public LocalDate getDataAbertura() { return dataAbertura; } // Data em que a conta foi aberta

    // Duas contas sao a mesma quando tem o mesmo id
    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof ContaBancaria conta)) {
            return false;
        }
        return Objects.equals(id, conta.id);
    }

    // Hash calculado so pelo id, igual ao criterio do equals
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Texto com id, numero e status
    @Override
    public String toString() {
        return "ContaBancaria[id=" + id + ", numero=" + numero + ", statusConta=" + statusConta + "]";
    }
}
