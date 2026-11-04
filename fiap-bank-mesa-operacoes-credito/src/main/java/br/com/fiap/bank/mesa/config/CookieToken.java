package br.com.fiap.bank.mesa.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.util.WebUtils;

// Le, grava e apaga o cookie que carrega o JWT da Plataforma de Credito
public final class CookieToken {

    // Nome do cookie que guarda o JWT
    private static final String NOME = "access_token";

    // Classe so de metodos estaticos, nao se instancia
    private CookieToken() {
    }

    // Devolve o token do cookie ou null quando o cookie nao existe
    public static String ler(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, NOME);
        return cookie != null ? cookie.getValue() : null;
    }

    // Cookie HttpOnly que expira junto com o token
    public static void gravar(HttpServletResponse response, String token, long validadeSegundos) {
        ResponseCookie cookie = ResponseCookie.from(NOME, token)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(validadeSegundos)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // Sobrescreve o cookie vazio com validade zero para o navegador descartar
    public static void apagar(HttpServletResponse response) {
        gravar(response, "", 0);
    }
}
