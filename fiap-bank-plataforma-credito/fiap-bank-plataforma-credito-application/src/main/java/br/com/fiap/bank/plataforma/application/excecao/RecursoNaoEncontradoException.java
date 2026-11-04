package br.com.fiap.bank.plataforma.application.excecao;

// Solicitacao ou conta que nao existe; a apresentacao responde 404
public class RecursoNaoEncontradoException extends RuntimeException {

    // Mensagem dizendo o que nao foi encontrado
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
