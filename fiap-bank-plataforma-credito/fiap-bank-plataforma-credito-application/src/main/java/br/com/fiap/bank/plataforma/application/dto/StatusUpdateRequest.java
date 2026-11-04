package br.com.fiap.bank.plataforma.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Corpo do PUT /api/v1/solicitacoes/{id}/status; so aceita APROVADO ou REPROVADO
public record StatusUpdateRequest(
    @NotNull(message = "Status e obrigatorio")
    @Pattern(regexp = "APROVADO|REPROVADO", message = "Status deve ser APROVADO ou REPROVADO")
    String status,

    @Size(max = 500, message = "Parecer do operador deve ter ate 500 caracteres")
    String parecerOperador
) {}
