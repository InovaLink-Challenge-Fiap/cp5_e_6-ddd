package br.com.fiap.bank.mesa.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

// Bloqueia as paginas da Mesa para quem ainda nao entrou com um usuario da Plataforma
public class AutenticacaoInterceptor implements HandlerInterceptor {

    // Sem token no cookie nem no header Authorization, manda para o login
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String cabecalho = request.getHeader("Authorization");
        boolean temCabecalho = cabecalho != null && cabecalho.startsWith("Bearer ");
        if (!temCabecalho && CookieToken.ler(request) == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
}
