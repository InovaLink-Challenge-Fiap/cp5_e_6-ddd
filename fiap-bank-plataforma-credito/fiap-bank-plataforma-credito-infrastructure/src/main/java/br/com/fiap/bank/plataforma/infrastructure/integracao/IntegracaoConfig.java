package br.com.fiap.bank.plataforma.infrastructure.integracao;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

// Liga os clientes Feign
@Configuration
@EnableFeignClients
public class IntegracaoConfig {
}
