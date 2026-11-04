package br.com.fiap.bank.plataforma.domain.excecao;

// CPF fora do formato de 11 digitos, com ou sem mascara
public class CpfInvalidoException extends DominioException {

    // Mensagem fixa explicando o formato aceito
    public CpfInvalidoException() {
        super("CPF invalido. Informe 11 digitos, com ou sem mascara");
    }
}
