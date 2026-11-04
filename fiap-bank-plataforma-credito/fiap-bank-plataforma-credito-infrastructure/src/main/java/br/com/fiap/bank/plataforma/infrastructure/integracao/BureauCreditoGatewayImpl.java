package br.com.fiap.bank.plataforma.infrastructure.integracao;

import br.com.fiap.bank.plataforma.domain.integracao.BureauCreditoGateway;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import org.springframework.stereotype.Component;

// Simula o Bureau de Credito (Serasa/SPC); o mesmo CPF sempre recebe o mesmo score
@Component
public class BureauCreditoGatewayImpl implements BureauCreditoGateway {

    // Soma os digitos do CPF e transforma em um score entre 400 e 950
    @Override
    public int consultarScore(Cpf cpf) {
        int soma = cpf.valor().chars().map(Character::getNumericValue).sum();
        return 400 + (soma * 9) % 551;
    }
}
