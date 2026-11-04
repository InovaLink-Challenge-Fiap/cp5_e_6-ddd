package br.com.fiap.bank.mesa.dto;

import java.util.List;

// Resposta do login da Plataforma: o JWT, a validade em segundos e os dados do usuario
public record LoginResponseDTO(
    String accessToken,
    String tokenType,
    Long expiresIn,
    String nome,
    String cpf,
    String conta,
    List<String> perfis
) {}
