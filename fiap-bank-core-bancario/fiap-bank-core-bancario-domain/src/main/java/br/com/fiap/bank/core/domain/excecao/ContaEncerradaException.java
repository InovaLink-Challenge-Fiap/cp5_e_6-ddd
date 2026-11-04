package br.com.fiap.bank.core.domain.excecao;

// Tentativa de movimentar uma conta que ja foi encerrada
public class ContaEncerradaException extends DominioException {

    // Mensagem com o numero da conta encerrada
    public ContaEncerradaException(String numero) {
        super("Conta " + numero + " esta encerrada");
    }
}
