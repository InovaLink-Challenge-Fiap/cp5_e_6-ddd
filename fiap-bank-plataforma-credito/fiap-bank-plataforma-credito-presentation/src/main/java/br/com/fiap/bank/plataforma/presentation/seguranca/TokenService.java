package br.com.fiap.bank.plataforma.presentation.seguranca;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

// Gera os tokens JWT do login; emissor e validade vem do application.yml
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.issuer}")
    private String issuer;

    @Getter
    @Value("${jwt.expiration-seconds}")
    private Long expirationSeconds;

    // Gera o token do cliente como no enunciado: CPF no sub e scope ROLE_CLIENTE
    public String gerarToken(String cpf, String conta, List<String> roles) {
        return montarToken(cpf, cpf, conta, roles, "ROLE_CLIENTE");
    }

    // Token do login: cliente usa a versao do enunciado; operador e admin levam o scope do perfil
    public String gerarToken(String login, String cpf, String conta, List<String> roles) {
        if (cpf != null) {
            return gerarToken(cpf, conta, roles);
        }
        return montarToken(login, null, null, roles, escopoDe(roles));
    }

    // Monta e assina o token; cpf e conta so entram quando tem valor
    private String montarToken(String sub, String cpf, String conta, List<String> roles, String scope) {
        Instant agora = Instant.now();

        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(expirationSeconds))
                .subject(sub)
                .claim("roles", roles)
                .claim("scope", scope);
        if (cpf != null) {
            claims.claim("cpf", cpf);
        }
        if (conta != null) {
            claims.claim("conta", conta);
        }

        return jwtEncoder.encode(JwtEncoderParameters.from(claims.build())).getTokenValue();
    }

    // Monta a claim scope com ROLE_ na frente de cada perfil, separados por espaco
    private String escopoDe(List<String> roles) {
        return roles.stream()
                .map(role -> "ROLE_" + role.replace("ROLE_", ""))
                .collect(Collectors.joining(" "));
    }
}
