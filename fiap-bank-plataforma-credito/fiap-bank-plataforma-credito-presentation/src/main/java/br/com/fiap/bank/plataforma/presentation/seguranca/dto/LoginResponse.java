package br.com.fiap.bank.plataforma.presentation.seguranca.dto;

import java.util.List;

// Resposta do login com o token Bearer e os dados do usuario
public record LoginResponse(
    String accessToken,
    String tokenType,
    Long expiresIn,
    String nome,
    String cpf,
    String conta,
    List<String> perfis
) {}
