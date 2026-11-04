package br.com.fiap.bank.plataforma.infrastructure.integracao;

import java.math.BigDecimal;

// JSON da conta devolvido pelo Core Bancario
public record ContaCoreResponse(
    String numeroConta,
    String agencia,
    BigDecimal saldo,
    String statusConta
) {}
