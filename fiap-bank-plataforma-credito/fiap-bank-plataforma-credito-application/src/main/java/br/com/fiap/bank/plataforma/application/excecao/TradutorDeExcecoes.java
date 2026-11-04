package br.com.fiap.bank.plataforma.application.excecao;

import br.com.fiap.bank.plataforma.domain.excecao.ContaNaoAtivaException;
import br.com.fiap.bank.plataforma.domain.excecao.ContaNaoEncontradaException;
import br.com.fiap.bank.plataforma.domain.excecao.CoreBancarioIndisponivelException;
import br.com.fiap.bank.plataforma.domain.excecao.CpfInvalidoException;
import br.com.fiap.bank.plataforma.domain.excecao.DominioException;
import br.com.fiap.bank.plataforma.domain.excecao.TransicaoDeStatusInvalidaException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// Traduz as excecoes do dominio que saem dos casos de uso, para a apresentacao nao depender do domain
@Aspect
@Component
public class TradutorDeExcecoes {

    // Troca a excecao do dominio pela excecao da aplicacao equivalente, com a mesma mensagem
    @AfterThrowing(pointcut = "execution(public * br.com.fiap.bank.plataforma.application.service..*(..))", throwing = "excecao")
    public void traduzir(DominioException excecao) {
        throw switch (excecao) {
            case CpfInvalidoException cpfInvalido -> new DadosInvalidosException(cpfInvalido.getMessage());
            case TransicaoDeStatusInvalidaException transicao -> new ConflitoDeNegocioException(transicao.getMessage());
            case ContaNaoEncontradaException conta -> new RecursoNaoEncontradoException(conta.getMessage());
            case ContaNaoAtivaException contaInativa -> new ConflitoDeNegocioException(contaInativa.getMessage());
            case CoreBancarioIndisponivelException core -> new ServicoIndisponivelException(core.getMessage(), core.getCause());
            default -> new DadosInvalidosException(excecao.getMessage());
        };
    }
}
