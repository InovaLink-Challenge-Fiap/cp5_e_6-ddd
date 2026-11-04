package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.excecao.CpfInvalidoException;

// Objeto de valor do CPF, guardado so com os 11 digitos
public record Cpf(String valor) {

    // 11 digitos seguidos ou no formato 000.000.000-00
    public static final String FORMATO = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";

    // Rejeita o que estiver fora do FORMATO e tira a mascara
    public Cpf {
        if (valor == null || !valor.matches(FORMATO)) {
            throw new CpfInvalidoException();
        }
        valor = valor.replaceAll("\\D", "");
    }
}
