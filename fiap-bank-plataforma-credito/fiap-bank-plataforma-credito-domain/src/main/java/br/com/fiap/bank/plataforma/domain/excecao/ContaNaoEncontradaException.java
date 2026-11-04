package br.com.fiap.bank.plataforma.domain.excecao;

// O Core Bancario nao tem conta para o CPF informado
public class ContaNaoEncontradaException extends DominioException {

    // Mensagem com o CPF consultado no Core
    public ContaNaoEncontradaException(String cpf) {
        super("Conta nao encontrada para o CPF: " + cpf);
    }
}
