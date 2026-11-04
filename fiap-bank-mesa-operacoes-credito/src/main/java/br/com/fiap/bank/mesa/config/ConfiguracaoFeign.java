package br.com.fiap.bank.mesa.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

// Configuracao do Feign que propaga o JWT da Mesa nas chamadas para a Plataforma de Credito
@Configuration
public class ConfiguracaoFeign {

    // Unica rota chamada sem token
    private static final String ROTA_LOGIN = "/api/auth/login";

    // Envia o Bearer do header Authorization ou, sem ele, do cookie access_token; o login vai sem token
    @Bean
    public RequestInterceptor interceptadorBearer() {
        return (RequestTemplate chamada) -> {
            if (ROTA_LOGIN.equals(chamada.path())) {
                return;
            }
            ServletRequestAttributes atributos = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (atributos != null) {
                HttpServletRequest request = atributos.getRequest();
                String tokenDoCabecalho = request.getHeader("Authorization");
                if (tokenDoCabecalho != null && tokenDoCabecalho.startsWith("Bearer ")) {
                    chamada.header("Authorization", tokenDoCabecalho);
                } else {
                    String tokenDoCookie = CookieToken.ler(request);
                    if (tokenDoCookie != null) {
                        chamada.header("Authorization", "Bearer " + tokenDoCookie);
                    }
                }
            }
        };
    }
}
