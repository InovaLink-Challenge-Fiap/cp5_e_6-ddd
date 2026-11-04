package br.com.fiap.bank.plataforma.infrastructure.integracao;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Cliente Feign da API do Core Bancario; a URL vem de core.bancario.url
@FeignClient(name = "coreBancarioClient", url = "${core.bancario.url}")
public interface CoreBancarioFeignClient {

    // Busca no Core a conta do cliente pelo CPF
    @GetMapping("/contas/cliente/{cpf}")
    ContaCoreResponse consultarContaCliente(@PathVariable("cpf") String cpf);
}
