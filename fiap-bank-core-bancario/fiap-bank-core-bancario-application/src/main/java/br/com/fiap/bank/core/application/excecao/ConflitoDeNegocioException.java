package br.com.fiap.bank.core.application.excecao;

// Pedido que conflita com o estado atual: cadastro repetido, saldo insuficiente ou conta encerrada
public class ConflitoDeNegocioException extends RuntimeException {

    // Recebe a mensagem que vai no corpo da resposta
    public ConflitoDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
