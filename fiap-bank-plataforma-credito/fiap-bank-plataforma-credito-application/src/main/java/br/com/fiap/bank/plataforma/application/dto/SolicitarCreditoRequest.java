package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.Cpf;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

// Corpo da criacao de uma solicitacao, usado na API v1, no adaptador legado e na carga inicial
public record SolicitarCreditoRequest(
    @NotNull(message = "CPF e obrigatorio")
    @Pattern(regexp = Cpf.FORMATO, message = "CPF deve ter 11 digitos, com ou sem mascara")
    String cpfCliente,

    @NotBlank(message = "Nome do cliente e obrigatorio")
    @Size(min = 3, max = 150, message = "Nome do cliente deve ter de 3 a 150 caracteres")
    String nomeCliente,

    @NotBlank(message = "Tipo de credito e obrigatorio")
    @Size(max = 255, message = "Tipo de credito deve ter ate 255 caracteres")
    String tipoCredito,

    @NotNull(message = "Valor solicitado e obrigatorio")
    @Positive(message = "Valor solicitado deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Valor solicitado deve ter ate 13 digitos inteiros e 2 decimais")
    BigDecimal valorSolicitado,

    @NotNull(message = "Quantidade de parcelas e obrigatoria")
    @Min(value = 1, message = "Quantidade de parcelas deve ser de 1 a 480")
    @Max(value = 480, message = "Quantidade de parcelas deve ser de 1 a 480")
    Integer quantidadeParcelas,

    @NotNull(message = "Renda mensal e obrigatoria")
    @Positive(message = "Renda mensal deve ser maior que zero")
    @Digits(integer = 13, fraction = 2, message = "Renda mensal deve ter ate 13 digitos inteiros e 2 decimais")
    BigDecimal rendaMensal
) {}
