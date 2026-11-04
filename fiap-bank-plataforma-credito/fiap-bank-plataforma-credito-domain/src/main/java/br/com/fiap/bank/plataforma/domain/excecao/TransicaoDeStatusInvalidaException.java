package br.com.fiap.bank.plataforma.domain.excecao;

import br.com.fiap.bank.plataforma.domain.model.StatusSolicitacao;

// Mudanca de status que a regra nao permite, como aprovar uma solicitacao ja reprovada
public class TransicaoDeStatusInvalidaException extends DominioException {

    // Mensagem com o status atual e o pretendido
    public TransicaoDeStatusInvalidaException(StatusSolicitacao atual, StatusSolicitacao destino) {
        super("Transicao de status invalida: " + atual + " para " + destino);
    }
}
