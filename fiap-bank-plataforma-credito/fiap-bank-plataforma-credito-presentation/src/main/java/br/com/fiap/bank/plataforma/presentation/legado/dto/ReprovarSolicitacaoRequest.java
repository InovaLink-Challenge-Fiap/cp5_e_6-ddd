package br.com.fiap.bank.plataforma.presentation.legado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Corpo da rota legada reprovarSolicitacao
public record ReprovarSolicitacaoRequest(
    @NotNull(message = "Id da solicitacao e obrigatorio")
    Long solicitacaoId,

    @Size(max = 500, message = "Motivo da reprovacao deve ter ate 500 caracteres")
    String motivoReprovacao
) {}
