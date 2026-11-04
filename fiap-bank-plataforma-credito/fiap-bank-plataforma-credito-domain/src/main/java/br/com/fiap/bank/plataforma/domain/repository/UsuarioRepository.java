package br.com.fiap.bank.plataforma.domain.repository;

import br.com.fiap.bank.plataforma.domain.model.Usuario;

import java.util.Optional;

// Porta de persistencia dos usuarios
public interface UsuarioRepository {

    // Busca o usuario pelo login
    Optional<Usuario> buscarPorLogin(String login);

    // Grava o usuario e devolve a versao salva
    Usuario salvar(Usuario usuario);
}
