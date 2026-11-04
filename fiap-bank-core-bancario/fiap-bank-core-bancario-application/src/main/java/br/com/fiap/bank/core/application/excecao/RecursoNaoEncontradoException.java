package br.com.fiap.bank.core.application.excecao;

// Cliente ou conta que nao existe com o id, CPF ou numero informado
public class RecursoNaoEncontradoException extends RuntimeException {

    // Recebe a mensagem que vai no corpo da resposta
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
