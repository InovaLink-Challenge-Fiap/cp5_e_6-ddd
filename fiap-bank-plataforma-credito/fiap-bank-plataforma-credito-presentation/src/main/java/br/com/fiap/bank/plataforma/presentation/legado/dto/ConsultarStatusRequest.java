package br.com.fiap.bank.plataforma.presentation.legado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

// Corpo das rotas legadas que recebem so o CPF do cliente
public record ConsultarStatusRequest(
    @NotNull(message = "CPF e obrigatorio")
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}", message = "CPF deve ter 11 digitos, com ou sem mascara")
    String cpfCliente
) {}
