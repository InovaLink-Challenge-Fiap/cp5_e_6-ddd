package br.com.fiap.bank.plataforma.presentation.seguranca;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

// Repassa os erros 401 e 403 dos filtros de seguranca ao GlobalExceptionHandler
@Component
public class TratadorDeErrosDeSeguranca implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final HandlerExceptionResolver resolvedorExcecoes;

    // Construtor escrito a mao por causa do @Qualifier
    public TratadorDeErrosDeSeguranca(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolvedorExcecoes) {
        this.resolvedorExcecoes = resolvedorExcecoes;
    }

    // Sem token ou com token invalido: 401 com o cabecalho WWW-Authenticate: Bearer
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException excecao) {
        response.setHeader(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        resolvedorExcecoes.resolveException(request, response, null, excecao);
    }

    // Token valido sem permissao: 403
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException excecao) {
        resolvedorExcecoes.resolveException(request, response, null, excecao);
    }
}
