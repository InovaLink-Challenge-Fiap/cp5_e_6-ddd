package br.com.fiap.bank.core.infrastructure.persistencia;

import br.com.fiap.bank.core.domain.entidade.Cliente;
import br.com.fiap.bank.core.domain.entidade.ContaBancaria;
import br.com.fiap.bank.core.domain.objetovalor.Cpf;
import br.com.fiap.bank.core.domain.objetovalor.Email;
import br.com.fiap.bank.core.domain.objetovalor.Saldo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

// Converte entre os objetos do dominio e as entidades JPA
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class MapeadorPersistencia {

    // Cliente do dominio para ClienteEntity, com CPF e email em texto e a conta quando houver
    static ClienteEntity paraEntidade(Cliente cliente) {
        return new ClienteEntity(
                cliente.getId(),
                cliente.getCpf().valor(),
                cliente.getNome(),
                cliente.getEmail().valor(),
                cliente.getDataNascimento(),
                cliente.getRendaMensal(),
                cliente.possuiConta() ? paraEntidade(cliente.getConta()) : null
        );
    }

    // ClienteEntity para Cliente do dominio, recriando CPF, email e a conta quando houver
    static Cliente paraDominio(ClienteEntity entidade) {
        return new Cliente(
                entidade.getId(),
                new Cpf(entidade.getCpf()),
                entidade.getNome(),
                new Email(entidade.getEmail()),
                entidade.getDataNascimento(),
                entidade.getRendaMensal(),
                entidade.getConta() != null ? paraDominio(entidade.getConta()) : null
        );
    }

    // Conta do dominio para ContaBancariaEntity, com o saldo em BigDecimal
    private static ContaBancariaEntity paraEntidade(ContaBancaria conta) {
        return new ContaBancariaEntity(
                conta.getId(),
                conta.getNumero(),
                conta.getAgencia(),
                conta.getTipoConta(),
                conta.getSaldo().valor(),
                conta.getStatusConta(),
                conta.getDataAbertura()
        );
    }

    // ContaBancariaEntity para conta do dominio, recriando o saldo como objeto de valor
    private static ContaBancaria paraDominio(ContaBancariaEntity entidade) {
        return new ContaBancaria(
                entidade.getId(),
                entidade.getNumeroConta(),
                entidade.getAgencia(),
                entidade.getTipoConta(),
                new Saldo(entidade.getSaldo()),
                entidade.getStatusConta(),
                entidade.getDataAbertura()
        );
    }
}
