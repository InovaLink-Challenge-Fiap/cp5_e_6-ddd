package br.com.fiap.bank.plataforma.presentation.seguranca;

import br.com.fiap.bank.plataforma.application.seguranca.CodificadorSenha;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// Implementa o CodificadorSenha da application com BCrypt do Spring Security
@Component
public class CodificadorSenhaBCrypt implements CodificadorSenha {

    private final BCryptPasswordEncoder codificador = new BCryptPasswordEncoder();

    // Confere a senha digitada com o hash BCrypt guardado
    @Override
    public boolean confere(String senha, String hash) {
        return codificador.matches(senha, hash);
    }

    // Gera o hash BCrypt da senha
    @Override
    public String codificar(String senha) {
        return codificador.encode(senha);
    }
}
