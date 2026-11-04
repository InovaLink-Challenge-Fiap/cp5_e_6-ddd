package br.com.fiap.bank.plataforma.domain.integracao;

import br.com.fiap.bank.plataforma.domain.model.ContaCliente;
import br.com.fiap.bank.plataforma.domain.model.Cpf;

// Porta para o Core Bancario, que e o dono das contas dos clientes
public interface CoreBancarioGateway {

    // Busca a conta do CPF; lanca excecao se ela nao existir ou se o Core estiver fora
    ContaCliente consultarConta(Cpf cpf);
}
