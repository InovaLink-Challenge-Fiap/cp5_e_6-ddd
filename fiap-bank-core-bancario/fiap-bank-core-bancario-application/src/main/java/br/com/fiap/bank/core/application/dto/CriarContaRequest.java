package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.objetovalor.TipoConta;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

// Corpo do POST de conta; a agencia e opcional e vira 0001 quando nao vem
public record CriarContaRequest(
    @NotBlank(message = "CPF do cliente e obrigatorio")
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve ter 11 digitos ou o formato XXX.XXX.XXX-XX")
    String cpfCliente,

    @Pattern(regexp = "\\d{4}", message = "Agencia deve ter exatamente 4 digitos")
    String agencia,

    @NotNull(message = "Tipo de conta e obrigatorio")
    TipoConta tipoConta,

    @NotNull(message = "Saldo inicial e obrigatorio")
    @PositiveOrZero(message = "Saldo inicial nao pode ser negativo")
    @Digits(integer = 13, fraction = 2, message = "Saldo inicial deve ter no maximo 13 digitos inteiros e 2 decimais")
    BigDecimal saldoInicial
) {}
