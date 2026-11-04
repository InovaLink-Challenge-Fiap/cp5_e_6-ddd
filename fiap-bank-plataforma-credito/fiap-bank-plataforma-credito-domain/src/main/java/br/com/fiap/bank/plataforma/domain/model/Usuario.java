package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.excecao.DominioException;

import java.util.Set;

// Usuario que faz login na plataforma; cpf e conta ficam nulos para operador e admin
public class Usuario {

    private final String login;
    private final String senhaHash;
    private final String nome;
    private final Cpf cpf;
    private final String conta;
    private final Set<Perfil> perfis;

    // Exige login, senha, nome e ao menos um perfil; guarda uma copia imutavel dos perfis
    public Usuario(String login, String senhaHash, String nome, Cpf cpf, String conta, Set<Perfil> perfis) {
        if (login == null || login.isBlank()) {
            throw new DominioException("Login e obrigatorio");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new DominioException("Senha e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new DominioException("Nome e obrigatorio");
        }
        if (perfis == null || perfis.isEmpty()) {
            throw new DominioException("Usuario precisa de ao menos um perfil");
        }
        this.login = login;
        this.senhaHash = senhaHash;
        this.nome = nome;
        this.cpf = cpf;
        this.conta = conta;
        this.perfis = Set.copyOf(perfis);
    }

    public String getLogin() { return login; } // Login usado na autenticacao
    public String getSenhaHash() { return senhaHash; } // Hash BCrypt da senha
    public String getNome() { return nome; } // Nome exibido nas telas
    public Cpf getCpf() { return cpf; } // CPF, nulo para operador e admin
    public String getConta() { return conta; } // Conta no Core, nula para operador e admin
    public Set<Perfil> getPerfis() { return perfis; } // Perfis do usuario, copia imutavel

    // Dois usuarios sao o mesmo quando tem o mesmo login
    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof Usuario usuario)) {
            return false;
        }
        return login.equals(usuario.login);
    }

    // Hash calculado so pelo login, igual ao criterio do equals
    @Override
    public int hashCode() {
        return login.hashCode();
    }
}
