package br.com.fiap.bank.core.domain.objetovalor;

import br.com.fiap.bank.core.domain.excecao.OperacaoInvalidaException;

import java.util.regex.Pattern;

// Email do cliente no formato nome@dominio.ext
public record Email(String valor) {

    private static final Pattern FORMATO = Pattern.compile("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");

    // Rejeita email nulo ou fora do formato
    public Email {
        if (valor == null || !FORMATO.matcher(valor).matches()) {
            throw new OperacaoInvalidaException("Email invalido: " + valor);
        }
    }

    // Texto do email do jeito que foi informado
    @Override
    public String toString() {
        return valor;
    }
}
