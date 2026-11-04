package br.com.fiap.bank.plataforma.application.config;

import br.com.fiap.bank.plataforma.domain.service.MotorDeCredito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Registra os servicos de dominio como beans, ja que o dominio nao conhece o Spring
@Configuration
public class DominioConfig {

    // Disponibiliza o MotorDeCredito para os casos de uso
    @Bean
    public MotorDeCredito motorDeCredito() {
        return new MotorDeCredito();
    }
}
