package br.com.fiap.bank.plataforma.domain.integracao;

import br.com.fiap.bank.plataforma.domain.model.Cpf;

// Porta para o CRM corporativo, que guarda o historico do cliente com o banco
public interface CrmGateway {

    // Informa se o historico do CPF no CRM registra inadimplencia
    boolean possuiInadimplencia(Cpf cpf);
}
