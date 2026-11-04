package br.com.fiap.bank.plataforma.application.excecao;

// Login inexistente ou senha que nao confere; a apresentacao responde 401
public class CredenciaisInvalidasException extends RuntimeException {

    // Mensagem fixa, sem dizer se foi o login ou a senha
    public CredenciaisInvalidasException() {
        super("Login ou senha invalidos");
    }
}
