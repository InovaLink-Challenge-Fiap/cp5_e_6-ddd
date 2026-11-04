package br.com.fiap.bank.plataforma.domain.integracao;

import br.com.fiap.bank.plataforma.domain.model.Cpf;

// Porta para os avisos enviados ao cliente (SMS/Email)
public interface NotificacaoGateway {

    // Envia a mensagem para o cliente dono do CPF
    void notificar(Cpf cpf, String mensagem);
}
