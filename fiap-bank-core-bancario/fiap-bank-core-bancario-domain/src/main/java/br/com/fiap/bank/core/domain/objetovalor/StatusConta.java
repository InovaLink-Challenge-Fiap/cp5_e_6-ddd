package br.com.fiap.bank.core.domain.objetovalor;

// Situacao da conta; so conta ATIVA pode movimentar saldo
public enum StatusConta {
    ATIVA,
    BLOQUEADA,
    ENCERRADA
}
