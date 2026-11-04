package br.com.fiap.bank.plataforma.domain.excecao;

// Falha de comunicacao com o Core Bancario; guarda a causa original para o log
public class CoreBancarioIndisponivelException extends DominioException {

    // Mensagem fixa mais a causa original
    public CoreBancarioIndisponivelException(Throwable causa) {
        super("Core Bancario indisponivel no momento", causa);
    }
}
