package br.com.fiap.bank.plataforma.presentation.seguranca;

import br.com.fiap.bank.plataforma.application.dto.UsuarioAutenticado;
import br.com.fiap.bank.plataforma.application.service.AutenticacaoService;
import br.com.fiap.bank.plataforma.presentation.seguranca.dto.LoginRequest;
import br.com.fiap.bank.plataforma.presentation.seguranca.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Login da plataforma em /api/auth, a unica rota da API que nao pede token
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AutenticacaoController {

    private final AutenticacaoService autenticacaoService;
    private final TokenService tokenService;

    // Confere as credenciais e devolve o token Bearer com os dados do usuario
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        UsuarioAutenticado usuario = autenticacaoService.autenticar(request.login(), request.senha());
        List<String> perfis = usuario.perfis().stream().sorted().toList();
        String token = tokenService.gerarToken(usuario.login(), usuario.cpf(), usuario.conta(), perfis);
        return new LoginResponse(token, "Bearer", tokenService.getExpirationSeconds(),
                usuario.nome(), usuario.cpf(), usuario.conta(), perfis);
    }
}
