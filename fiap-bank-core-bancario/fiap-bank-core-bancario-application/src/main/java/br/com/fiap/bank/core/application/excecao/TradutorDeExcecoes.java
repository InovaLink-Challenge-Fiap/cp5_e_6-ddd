package br.com.fiap.bank.core.application.excecao;

import br.com.fiap.bank.core.domain.excecao.ContaEncerradaException;
import br.com.fiap.bank.core.domain.excecao.ContaJaCadastradaException;
import br.com.fiap.bank.core.domain.excecao.CpfInvalidoException;
import br.com.fiap.bank.core.domain.excecao.DominioException;
import br.com.fiap.bank.core.domain.excecao.OperacaoInvalidaException;
import br.com.fiap.bank.core.domain.excecao.SaldoInsuficienteException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// Traduz as excecoes do dominio lancadas nos services para que a presentation nao dependa do domain
@Aspect
@Component
public class TradutorDeExcecoes {

    // Quando um service lanca excecao do dominio, lanca no lugar a excecao equivalente do application
    @AfterThrowing(pointcut = "execution(public * br.com.fiap.bank.core.application.service..*(..))", throwing = "excecao")
    public void traduzir(DominioException excecao) {
        throw equivalente(excecao);
    }

    // Saldo insuficiente, conta encerrada e conta ja aberta viram conflito; o resto vira dado invalido
    private RuntimeException equivalente(DominioException excecao) {
        return switch (excecao) {
            case SaldoInsuficienteException saldo -> new ConflitoDeNegocioException(saldo.getMessage());
            case ContaEncerradaException encerrada -> new ConflitoDeNegocioException(encerrada.getMessage());
            case ContaJaCadastradaException cadastrada -> new ConflitoDeNegocioException(cadastrada.getMessage());
            case CpfInvalidoException cpf -> new DadosInvalidosException(cpf.getMessage());
            case OperacaoInvalidaException operacao -> new DadosInvalidosException(operacao.getMessage());
            default -> new DadosInvalidosException(excecao.getMessage());
        };
    }
}
