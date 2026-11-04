package br.com.fiap.bank.plataforma.domain.integracao;

import br.com.fiap.bank.plataforma.domain.model.Cpf;

// Porta para o Bureau de Credito (Serasa/SPC)
public interface BureauCreditoGateway {

    // Devolve o score de credito do CPF
    int consultarScore(Cpf cpf);
}
