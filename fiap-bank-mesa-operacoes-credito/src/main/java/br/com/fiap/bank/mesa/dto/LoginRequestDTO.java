package br.com.fiap.bank.mesa.dto;

// Corpo enviado para POST /api/auth/login da Plataforma
public record LoginRequestDTO(
    String login,
    String senha
) {}
