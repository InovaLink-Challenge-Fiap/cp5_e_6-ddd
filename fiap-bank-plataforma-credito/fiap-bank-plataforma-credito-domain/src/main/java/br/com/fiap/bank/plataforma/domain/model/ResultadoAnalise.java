package br.com.fiap.bank.plataforma.domain.model;

// Status e parecer que o Motor de Credito devolve para uma proposta
public record ResultadoAnalise(
    StatusSolicitacao status,
    String parecer
) {}
