package br.com.fiap.bank.mesa.controller;

import br.com.fiap.bank.mesa.client.AutenticacaoFeignClient;
import br.com.fiap.bank.mesa.config.CookieToken;
import br.com.fiap.bank.mesa.dto.LoginRequestDTO;
import br.com.fiap.bank.mesa.dto.LoginResponseDTO;
import feign.FeignException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// Tela de login, entrada e saida da Mesa usando a autenticacao da Plataforma de Credito
@Controller
public class LoginController {

    // Perfis que podem entrar na Mesa
    private static final List<String> PERFIS_PERMITIDOS = List.of("OPERADOR", "ADMIN");

    private final AutenticacaoFeignClient autenticacaoClient;

    // Guarda o cliente Feign de autenticacao
    public LoginController(AutenticacaoFeignClient autenticacaoClient) {
        this.autenticacaoClient = autenticacaoClient;
    }

    // Abre o login; o parametro erro escolhe a mensagem mostrada no template
    @GetMapping("/login")
    public String telaLogin(@RequestParam(required = false) String erro, Model model) {
        model.addAttribute("erro", erro);
        return "login";
    }

    // So grava o cookie se o usuario tiver perfil OPERADOR ou ADMIN
    @PostMapping("/login")
    public String entrar(@RequestParam("usuario") String usuario,
                         @RequestParam("senha") String senha,
                         HttpServletResponse response) {
        LoginResponseDTO resposta;
        try {
            resposta = autenticacaoClient.autenticar(new LoginRequestDTO(usuario, senha));
        } catch (FeignException.Unauthorized | FeignException.BadRequest e) {
            return "redirect:/login?erro=credenciais";
        } catch (FeignException e) {
            System.err.println("[MESA DE OPERACOES] Falha ao autenticar na Plataforma de Credito: " + e.getMessage());
            return "redirect:/login?erro=indisponivel";
        }

        if (resposta.perfis().stream().noneMatch(PERFIS_PERMITIDOS::contains)) {
            return "redirect:/login?erro=perfil";
        }

        CookieToken.gravar(response, resposta.accessToken(), resposta.expiresIn());
        return "redirect:/dashboard";
    }

    // Apaga o cookie do token e volta para o login
    @GetMapping("/logout")
    public String sair(HttpServletResponse response) {
        CookieToken.apagar(response);
        return "redirect:/login";
    }
}
