package br.com.fiap.bank.plataforma.presentation.legado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Corpo da rota legada aprovarSolicitacao
public record AprovarSolicitacaoRequest(
    @NotNull(message = "Id da solicitacao e obrigatorio")
    Long solicitacaoId,

    @Size(max = 500, message = "Parecer do operador deve ter ate 500 caracteres")
    String parecerOperador
) {}
