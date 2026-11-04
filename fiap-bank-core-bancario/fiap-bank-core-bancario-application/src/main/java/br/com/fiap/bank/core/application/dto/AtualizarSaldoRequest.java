package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// Corpo do PUT de saldo: operacao DEPOSITO ou SAQUE e valor maior que zero
public record AtualizarSaldoRequest(
    @NotBlank(message = "Operacao e obrigatoria")
    @Pattern(regexp = "(?i)DEPOSITO|SAQUE", message = "Operacao deve ser DEPOSITO ou SAQUE")
    String operacao,

    @NotNull(message = "Valor e obrigatorio")
    @Positive(message = "Valor deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor deve ter no maximo 13 digitos inteiros e 2 decimais")
    BigDecimal valor
) {}
