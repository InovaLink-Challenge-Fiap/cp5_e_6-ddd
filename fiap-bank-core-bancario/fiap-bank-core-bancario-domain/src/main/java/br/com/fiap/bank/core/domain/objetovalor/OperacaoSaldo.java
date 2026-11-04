package br.com.fiap.bank.core.domain.objetovalor;

import br.com.fiap.bank.core.domain.excecao.OperacaoInvalidaException;

// Operacoes aceitas na atualizacao de saldo da conta
public enum OperacaoSaldo {
    DEPOSITO,
    SAQUE;

    // Converte o texto da requisicao sem diferenciar maiuscula de minuscula; valor desconhecido e erro
    public static OperacaoSaldo de(String operacao) {
        for (OperacaoSaldo opcao : values()) {
            if (opcao.name().equalsIgnoreCase(operacao)) {
                return opcao;
            }
        }
        throw new OperacaoInvalidaException("Operacao de saldo desconhecida: " + operacao);
    }
}
