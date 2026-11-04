package br.com.fiap.bank.plataforma.presentation.legado.dto;

import jakarta.validation.constraints.NotNull;

// Corpo da rota legada buscarSolicitacaoPorId
public record BuscarSolicitacaoRequest(
    @NotNull(message = "Id da solicitacao e obrigatorio")
    Long solicitacaoId
) {}
