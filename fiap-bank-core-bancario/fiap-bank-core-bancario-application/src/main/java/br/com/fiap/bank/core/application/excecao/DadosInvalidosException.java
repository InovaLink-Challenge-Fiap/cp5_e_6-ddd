package br.com.fiap.bank.core.application.excecao;

// Dado recebido fora do formato ou que quebra uma regra do dominio
public class DadosInvalidosException extends RuntimeException {

    // Recebe a mensagem que vai no corpo da resposta
    public DadosInvalidosException(String mensagem) {
        super(mensagem);
    }
}
