package br.com.fiap.bank.plataforma.application.excecao;

// Dado de entrada que a regra de negocio nao aceita; a apresentacao responde 400
public class DadosInvalidosException extends RuntimeException {

    // Mensagem com o dado recusado
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
