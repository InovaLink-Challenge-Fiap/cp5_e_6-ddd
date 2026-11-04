package br.com.fiap.bank.plataforma.application.excecao;

// Servico externo fora do ar, como o Core Bancario; a apresentacao responde 503
public class ServicoIndisponivelException extends RuntimeException {

    // Mensagem para o cliente mais a causa original, que vai so para o log
    public ServicoIndisponivelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
