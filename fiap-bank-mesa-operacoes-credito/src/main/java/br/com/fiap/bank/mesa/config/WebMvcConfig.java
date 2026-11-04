package br.com.fiap.bank.mesa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Registra o AutenticacaoInterceptor nas rotas da Mesa
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Login, logout, pagina de erro e arquivos estaticos ficam liberados sem token
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AutenticacaoInterceptor())
                .excludePathPatterns("/login", "/logout", "/error", "/css/**", "/js/**", "/images/**", "/favicon.ico");
    }
}
