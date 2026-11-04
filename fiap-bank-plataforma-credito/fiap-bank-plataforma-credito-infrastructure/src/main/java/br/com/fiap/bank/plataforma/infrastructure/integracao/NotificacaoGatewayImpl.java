package br.com.fiap.bank.plataforma.infrastructure.integracao;

import br.com.fiap.bank.plataforma.domain.integracao.NotificacaoGateway;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import org.springframework.stereotype.Component;

// Simula o envio de SMS/Email escrevendo no console
@Component
public class NotificacaoGatewayImpl implements NotificacaoGateway {

    // Escreve no console a mensagem que iria para o cliente
    @Override
    public void notificar(Cpf cpf, String mensagem) {
        System.out.println("[SISTEMA DE NOTIFICACOES] Notificacao enviada para CPF " + mascarar(cpf) + ": " + mensagem);
    }
    // Esconde os primeiros digitos do CPF no log
    private static String mascarar(Cpf cpf) {
        String valor = cpf.valor();
        return "***.***." + valor.substring(6, 9) + "-" + valor.substring(9);
    }
}
