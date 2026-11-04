package br.com.fiap.bank.plataforma.application.service;

import br.com.fiap.bank.plataforma.application.dto.UsuarioAutenticado;
import br.com.fiap.bank.plataforma.application.excecao.CredenciaisInvalidasException;
import br.com.fiap.bank.plataforma.application.seguranca.CodificadorSenha;
import br.com.fiap.bank.plataforma.domain.model.Usuario;
import br.com.fiap.bank.plataforma.domain.repository.UsuarioRepository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Caso de uso do login
@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;
    private final CodificadorSenha codificadorSenha;
    private String hashFalso;

    // Confere login e senha; devolve o usuario ou lanca CredenciaisInvalidasException
    @Transactional(readOnly = true)
    public UsuarioAutenticado autenticar(String login, String senha) {
        if (login == null || senha == null) {
            throw new CredenciaisInvalidasException();
        }
        Optional<Usuario> usuario = usuarioRepository.buscarPorLogin(login);
        String hash = usuario.map(Usuario::getSenhaHash).orElseGet(this::hashFalso);
        boolean senhaConfere = codificadorSenha.confere(senha, hash);
        if (usuario.isEmpty() || !senhaConfere) {
            throw new CredenciaisInvalidasException();
        }
        return UsuarioAutenticado.de(usuario.get());
    }

    // Hash usado quando o login nao existe, para a resposta demorar o mesmo tempo
    private String hashFalso() {
        if (hashFalso == null) {
            hashFalso = codificadorSenha.codificar("senha-falsa");
        }
        return hashFalso;
    }
}
