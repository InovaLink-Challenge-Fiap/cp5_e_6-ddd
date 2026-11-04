package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.ContaCliente;

import java.math.BigDecimal;

// Resposta com os dados da conta do cliente no Core Bancario
public record ContaClienteResponse(
    String statusConta,
    BigDecimal saldo,
    String agencia,
    String numeroConta
) {
    // Monta a resposta a partir da conta do dominio
    public static ContaClienteResponse de(ContaCliente conta) {
        return new ContaClienteResponse(conta.statusConta(), conta.saldo(), conta.agencia(), conta.numeroConta());
    }
}
