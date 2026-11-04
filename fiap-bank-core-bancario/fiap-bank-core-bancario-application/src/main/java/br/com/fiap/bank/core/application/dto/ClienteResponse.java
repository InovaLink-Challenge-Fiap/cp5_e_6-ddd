package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.entidade.Cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Dados do cliente devolvidos pela API
public record ClienteResponse(
    UUID id,
    String nome,
    String cpf,
    String email,
    LocalDate dataNascimento,
    BigDecimal rendaMensal
) {

    // Monta a resposta a partir do Cliente do dominio
    public static ClienteResponse de(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf().valor(),
                cliente.getEmail().valor(),
                cliente.getDataNascimento(),
                cliente.getRendaMensal()
        );
    }
}
