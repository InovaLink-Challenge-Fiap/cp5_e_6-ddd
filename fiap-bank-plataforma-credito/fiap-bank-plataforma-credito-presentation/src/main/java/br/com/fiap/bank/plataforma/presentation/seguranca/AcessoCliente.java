package br.com.fiap.bank.plataforma.presentation.seguranca;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

// Usado nas expressoes do @PreAuthorize para o cliente acessar apenas os proprios dados
@Component
public class AcessoCliente {

    // 11 digitos seguidos ou no formato 000.000.000-00, a mesma regra dos DTOs
    private static final String FORMATO_CPF = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}";

    // Compara o CPF com a claim cpf do token; CPF malformado responde false para nao virar erro 500
    public boolean mesmoCpf(String cpf) {
        if (cpf == null || !cpf.matches(FORMATO_CPF)) {
            return false;
        }
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        return autenticacao != null
                && autenticacao.getPrincipal() instanceof Jwt jwt
                && cpf.replaceAll("\\D", "").equals(jwt.getClaimAsString("cpf"));
    }
}
