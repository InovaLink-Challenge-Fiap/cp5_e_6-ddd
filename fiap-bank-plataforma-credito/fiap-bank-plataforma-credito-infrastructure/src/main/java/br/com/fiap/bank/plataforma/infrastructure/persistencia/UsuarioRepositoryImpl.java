package br.com.fiap.bank.plataforma.infrastructure.persistencia;

import br.com.fiap.bank.plataforma.domain.model.Cpf;
import br.com.fiap.bank.plataforma.domain.model.Usuario;
import br.com.fiap.bank.plataforma.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Optional;

// Implementa a porta de usuarios com JPA, convertendo entre dominio e entidade
@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository repositorioJpa;

    // Busca pelo login e converte para o dominio
    @Override
    public Optional<Usuario> buscarPorLogin(String login) {
        return repositorioJpa.findById(login).map(this::paraDominio);
    }

    // Grava a entidade e devolve o usuario salvo
    @Override
    public Usuario salvar(Usuario usuario) {
        return paraDominio(repositorioJpa.save(paraEntidade(usuario)));
    }

    // Copia o usuario para a entidade JPA
    private UsuarioEntity paraEntidade(Usuario usuario) {
        return new UsuarioEntity(
                usuario.getLogin(),
                usuario.getSenhaHash(),
                usuario.getNome(),
                usuario.getCpf() != null ? usuario.getCpf().valor() : null,
                usuario.getConta(),
                new HashSet<>(usuario.getPerfis())
        );
    }

    // Recria o usuario do dominio a partir da entidade
    private Usuario paraDominio(UsuarioEntity entidade) {
        return new Usuario(
                entidade.getLogin(),
                entidade.getSenhaHash(),
                entidade.getNome(),
                entidade.getCpf() != null ? new Cpf(entidade.getCpf()) : null,
                entidade.getConta(),
                entidade.getPerfis()
        );
    }
}
