package br.com.fiap.bank.plataforma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Classe principal da Plataforma de Credito; o pacote base cobre os beans dos quatro modulos
@SpringBootApplication
public class PlataformaCreditoApplication {

    // Sobe a aplicacao Spring Boot na porta do application.yml
    public static void main(String[] args) {
        SpringApplication.run(PlataformaCreditoApplication.class, args);
    }
}
