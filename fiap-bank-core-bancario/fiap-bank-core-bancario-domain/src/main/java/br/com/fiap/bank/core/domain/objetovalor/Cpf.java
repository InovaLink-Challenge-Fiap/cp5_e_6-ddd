package br.com.fiap.bank.core.domain.objetovalor;

import br.com.fiap.bank.core.domain.excecao.CpfInvalidoException;

import java.util.regex.Pattern;

// CPF do cliente, aceito limpo ou com mascara e guardado so com os 11 digitos
public record Cpf(String valor) {

    private static final Pattern FORMATO = Pattern.compile("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");

    // Rejeita formato invalido e tira a mascara; sem digito verificador porque os CPFs de teste sao ficticios
    public Cpf {
        if (valor == null || !FORMATO.matcher(valor).matches()) {
            throw new CpfInvalidoException(valor);
        }
        valor = valor.replaceAll("\\D", "");
    }

    // Texto do CPF so com os 11 digitos
    @Override
    public String toString() {
        return valor;
    }
}
