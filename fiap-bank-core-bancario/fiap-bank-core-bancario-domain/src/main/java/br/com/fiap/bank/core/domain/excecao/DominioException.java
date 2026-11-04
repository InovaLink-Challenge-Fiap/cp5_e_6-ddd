package br.com.fiap.bank.core.domain.excecao;

// Base das excecoes de regra de negocio do dominio
public abstract class DominioException extends RuntimeException {

    // Toda excecao do dominio nasce com a mensagem pronta
    protected DominioException(String mensagem) {
        super(mensagem);
    }
}
