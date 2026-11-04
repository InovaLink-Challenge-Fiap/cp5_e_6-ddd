package br.com.fiap.bank.plataforma.infrastructure.integracao;

import br.com.fiap.bank.plataforma.domain.integracao.CrmGateway;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import org.springframework.stereotype.Component;

// Simula o CRM corporativo
@Component
public class CrmGatewayImpl implements CrmGateway {

    // CPF terminado em 7 tem inadimplencia registrada no historico
    @Override
    public boolean possuiInadimplencia(Cpf cpf) {
        return cpf.valor().endsWith("7");
    }
}
