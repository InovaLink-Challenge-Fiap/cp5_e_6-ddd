package br.com.fiap.bank.plataforma.domain.excecao;

// Excecao base das regras de negocio; as demais excecoes do dominio estendem esta
public class DominioException extends RuntimeException {

    // So a mensagem
    public DominioException(String mensagem) {
        super(mensagem);
    }

    // Mensagem mais a causa original
    public DominioException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
