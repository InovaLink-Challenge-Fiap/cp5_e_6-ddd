package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.Perfil;
import br.com.fiap.bank.plataforma.domain.model.Usuario;

import java.util.Set;
import java.util.stream.Collectors;

// Dados do usuario que passou no login, sem o hash da senha e com os perfis pelo nome
public record UsuarioAutenticado(
    String login,
    String nome,
    String cpf,
    String conta,
    Set<String> perfis
) {
    // Monta a partir do Usuario do dominio; cpf fica nulo para operador e admin
    public static UsuarioAutenticado de(Usuario usuario) {
        return new UsuarioAutenticado(
            usuario.getLogin(),
            usuario.getNome(),
            usuario.getCpf() != null ? usuario.getCpf().valor() : null,
            usuario.getConta(),
            usuario.getPerfis().stream().map(Perfil::name).collect(Collectors.toUnmodifiableSet())
        );
    }
}
