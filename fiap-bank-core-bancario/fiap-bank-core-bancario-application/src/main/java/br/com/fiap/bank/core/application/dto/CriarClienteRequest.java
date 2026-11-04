package br.com.fiap.bank.core.application.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

// Corpo do POST e do PUT de cliente, validado com Bean Validation antes de chegar no service
public record CriarClienteRequest(
    @NotBlank(message = "CPF e obrigatorio")
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve ter 11 digitos ou o formato XXX.XXX.XXX-XX")
    String cpf,

    @NotBlank(message = "Nome e obrigatorio")
    @Size(min = 3, max = 150, message = "Nome deve ter entre 3 e 150 caracteres")
    String nome,

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    @Size(max = 150, message = "Email deve ter no maximo 150 caracteres")
    String email,

    @NotNull(message = "Data de nascimento e obrigatoria")
    @Past(message = "Data de nascimento deve estar no passado")
    LocalDate dataNascimento,

    @NotNull(message = "Renda mensal e obrigatoria")
    @PositiveOrZero(message = "Renda mensal nao pode ser negativa")
    @Digits(integer = 13, fraction = 2, message = "Renda mensal deve ter no maximo 13 digitos inteiros e 2 decimais")
    BigDecimal rendaMensal
) {}
