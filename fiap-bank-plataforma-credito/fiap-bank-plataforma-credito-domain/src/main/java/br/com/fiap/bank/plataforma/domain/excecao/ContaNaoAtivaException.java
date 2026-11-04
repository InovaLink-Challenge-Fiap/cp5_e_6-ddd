package br.com.fiap.bank.plataforma.domain.excecao;

// Conta do cliente que nao esta ativa no Core Bancario e por isso nao pode receber credito
public class ContaNaoAtivaException extends DominioException {

    // Mensagem com o status atual da conta
    public ContaNaoAtivaException(String statusConta) {
        super("A conta do cliente nao esta ativa no Core Bancario (status " + statusConta + ")");
    }
}
