package br.com.fiap.bank.plataforma.application.seguranca;

// Porta para gerar e conferir hash de senha; a implementacao com BCrypt fica no presentation
public interface CodificadorSenha {

    // Confere se a senha digitada bate com o hash guardado
    boolean confere(String senha, String hash);

    // Gera o hash da senha para guardar no banco
    String codificar(String senha);
}
