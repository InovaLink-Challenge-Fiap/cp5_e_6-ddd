package br.com.fiap.bank.plataforma.application.excecao;

// Operacao que conflita com o estado atual, como aprovar uma solicitacao ja reprovada; vira 409
public class ConflitoDeNegocioException extends RuntimeException {

    // Mensagem com o motivo do conflito
    public ConflitoDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
