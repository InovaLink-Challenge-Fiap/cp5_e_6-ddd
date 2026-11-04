package br.com.fiap.bank.plataforma.presentation.seguranca;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// Configura a plataforma como OAuth2 Resource Server stateless, com JWT assinado em HMAC
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.algorithm}")
    private String algoritmoJwt;

    @Value("${jwt.issuer}")
    private String emissorJwt;

    private final TratadorDeErrosDeSeguranca erroSegurancaHandler;

    // Libera /api/auth e o H2 Console, que abre em frames; o resto exige token e nao cria sessao
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(erroSegurancaHandler)
                        .accessDeniedHandler(erroSegurancaHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(erroSegurancaHandler)
                        .accessDeniedHandler(erroSegurancaHandler))
                .build();
    }

    // Confere a assinatura e a validade do token e exige que o emissor seja esta plataforma
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder decodificador = NimbusJwtDecoder.withSecretKey(chaveSecreta()).build();
        decodificador.setJwtValidator(JwtValidators.createDefaultWithIssuer(emissorJwt));
        return decodificador;
    }

    // Assina os tokens do login com a mesma chave HMAC do decoder
    @Bean
    public JwtEncoder jwtEncoder() {
        return NimbusJwtEncoder.withSecretKey(chaveSecreta()).build();
    }

    // Junta as authorities SCOPE_ da claim scope com as ROLE_ da claim roles
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter defaultAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(defaultAuthoritiesConverter.convert(jwt));
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null) {
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.replace("ROLE_", ""))));
            }
            return authorities;
        });
        return jwtAuthenticationConverter;
    }

    // Libera as origens http://localhost:* para o app Angular, cujo preflight OPTIONS chega sem token
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();
        configuracao.setAllowedOriginPatterns(List.of("http://localhost:*"));
        configuracao.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuracao.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource fonte = new UrlBasedCorsConfigurationSource();
        fonte.registerCorsConfiguration("/**", configuracao);
        return fonte;
    }

    // Monta a chave HMAC com o segredo e o algoritmo do application.yml
    private SecretKeySpec chaveSecreta() {
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), algoritmoJwt);
    }
}
