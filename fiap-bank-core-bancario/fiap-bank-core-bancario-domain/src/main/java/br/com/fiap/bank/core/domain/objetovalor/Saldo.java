package br.com.fiap.bank.core.domain.objetovalor;

import br.com.fiap.bank.core.domain.excecao.OperacaoInvalidaException;
import br.com.fiap.bank.core.domain.excecao.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.math.RoundingMode;

// Saldo da conta, nunca negativo e sempre com 2 casas decimais
public record Saldo(BigDecimal valor) {

    // Rejeita saldo nulo ou negativo e arredonda para 2 casas
    public Saldo {
        if (valor == null || valor.signum() < 0) {
            throw new OperacaoInvalidaException("Saldo nao pode ser negativo");
        }
        valor = valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    // Devolve um saldo novo com a quantia somada
    public Saldo adicionar(BigDecimal quantia) {
        return new Saldo(valor.add(quantia));
    }

    // Devolve um saldo novo com a quantia descontada; quantia acima do saldo e recusada
    public Saldo subtrair(BigDecimal quantia) {
        if (quantia.compareTo(valor) > 0) {
            throw new SaldoInsuficienteException();
        }
        return new Saldo(valor.subtract(quantia));
    }

    // Valor do saldo em texto com as 2 casas, sem notacao cientifica
    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
