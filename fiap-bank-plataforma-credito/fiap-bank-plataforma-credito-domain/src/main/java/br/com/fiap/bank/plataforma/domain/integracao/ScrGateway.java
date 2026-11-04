package br.com.fiap.bank.plataforma.domain.integracao;

import br.com.fiap.bank.plataforma.domain.model.Cpf;

// Porta para o SCR do Banco Central
public interface ScrGateway {

    // Informa se o CPF tem restricao ativa no SCR
    boolean possuiRestricoes(Cpf cpf);
}
