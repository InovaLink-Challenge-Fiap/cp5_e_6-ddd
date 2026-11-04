package br.com.fiap.bank.plataforma.infrastructure.integracao;

import br.com.fiap.bank.plataforma.domain.integracao.ScrGateway;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import org.springframework.stereotype.Component;

// Simula o SCR do Banco Central
@Component
public class ScrGatewayImpl implements ScrGateway {

    // CPF terminado em 9 tem restricao
    @Override
    public boolean possuiRestricoes(Cpf cpf) {
        return cpf.valor().endsWith("9");
    }
}
