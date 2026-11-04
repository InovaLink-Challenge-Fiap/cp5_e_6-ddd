package br.com.fiap.bank.plataforma.presentation.seguranca.dto;

import jakarta.validation.constraints.NotBlank;

// Corpo do POST /api/auth/login
public record LoginRequest(
    @NotBlank(message = "Login e obrigatorio")
    String login,

    @NotBlank(message = "Senha e obrigatoria")
    String senha
) {}
