package br.com.fiap.bank.core.application.dto;

import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.entidade.ContaBancaria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Dados da conta devolvidos pela API, com CPF e nome do cliente dono
public record ContaResponse(
    UUID id,
    String numeroConta,
    String agencia,
    String tipoConta,
    BigDecimal saldo,
    String statusConta,
    LocalDate dataAbertura,
    String cpfCliente,
    String nomeCliente
) {

    // Monta a resposta a partir do Cliente do dominio e da conta que ele tem
    public static ContaResponse de(Cliente cliente) {
        ContaBancaria conta = cliente.getConta();
        return new ContaResponse(
                conta.getId(),
                conta.getNumero(),
                conta.getAgencia(),
                conta.getTipoConta().name(),
                conta.getSaldo().valor(),
                conta.getStatusConta().name(),
                conta.getDataAbertura(),
                cliente.getCpf().valor(),
                cliente.getNome()
        );
    }
}
