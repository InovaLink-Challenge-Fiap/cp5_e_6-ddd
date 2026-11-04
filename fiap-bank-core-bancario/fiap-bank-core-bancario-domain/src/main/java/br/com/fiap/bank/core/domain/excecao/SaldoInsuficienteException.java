package br.com.fiap.bank.core.domain.excecao;

// Saque maior que o saldo disponivel na conta
public class SaldoInsuficienteException extends DominioException {

    // Mensagem fixa
    public SaldoInsuficienteException() {
        super("Saldo insuficiente para o saque");
    }
}
