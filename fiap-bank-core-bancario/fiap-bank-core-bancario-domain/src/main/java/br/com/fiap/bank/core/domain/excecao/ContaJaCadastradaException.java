package br.com.fiap.bank.core.domain.excecao;

import br.com.fiap.bank.core.domain.objetovalor.Cpf;

// Tentativa de abrir uma segunda conta para o mesmo cliente
public class ContaJaCadastradaException extends DominioException {

    // Mensagem com o CPF do cliente que ja tem conta
    public ContaJaCadastradaException(Cpf cpf) {
        super("Cliente com CPF " + cpf + " ja possui conta");
    }
}
