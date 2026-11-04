package br.com.fiap.bank.core.domain.excecao;

// CPF nulo ou fora do formato aceito
public class CpfInvalidoException extends DominioException {

    // Mensagem com o valor recebido
    public CpfInvalidoException(String cpf) {
        super("CPF invalido: " + cpf);
    }
}
