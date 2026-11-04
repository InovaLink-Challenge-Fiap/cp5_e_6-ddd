package br.com.fiap.bank.core.domain.excecao;

// Dado ou operacao que quebra uma regra do dominio
public class OperacaoInvalidaException extends DominioException {

    // Recebe a mensagem da regra que foi quebrada
    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }
}
