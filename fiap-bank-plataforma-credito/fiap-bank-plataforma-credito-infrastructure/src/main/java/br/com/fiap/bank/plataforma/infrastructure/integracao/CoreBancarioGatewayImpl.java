package br.com.fiap.bank.plataforma.infrastructure.integracao;

import br.com.fiap.bank.plataforma.domain.excecao.ContaNaoEncontradaException;
import br.com.fiap.bank.plataforma.domain.excecao.CoreBancarioIndisponivelException;
import br.com.fiap.bank.plataforma.domain.integracao.CoreBancarioGateway;
import br.com.fiap.bank.plataforma.domain.model.ContaCliente;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// Implementa a porta do Core Bancario chamando a API dele pelo Feign
@Component
@RequiredArgsConstructor
public class CoreBancarioGatewayImpl implements CoreBancarioGateway {

    private final CoreBancarioFeignClient clienteFeign;

    // 404 do Core vira conta nao encontrada; qualquer outra falha vira Core indisponivel
    @Override
    public ContaCliente consultarConta(Cpf cpf) {
        try {
            ContaCoreResponse conta = clienteFeign.consultarContaCliente(cpf.valor());
            return new ContaCliente(conta.statusConta(), conta.saldo(), conta.agencia(), conta.numeroConta());
        } catch (FeignException.NotFound e) {
            throw new ContaNaoEncontradaException(cpf.valor());
        } catch (FeignException e) {
            throw new CoreBancarioIndisponivelException(e);
        }
    }
}
