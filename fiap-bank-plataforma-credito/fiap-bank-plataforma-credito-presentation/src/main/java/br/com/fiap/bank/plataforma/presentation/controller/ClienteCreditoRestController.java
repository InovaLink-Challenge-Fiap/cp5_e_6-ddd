package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.ContaClienteResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitacaoResponse;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// API v1 das consultas por cliente; o cliente so acessa o proprio CPF
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteCreditoRestController {

    private final SolicitacaoCreditoService solicitacaoService;

    // Solicitacoes de um CPF; operador e admin consultam qualquer cliente
    @GetMapping("/{cpf}/solicitacoes")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR') or (hasAuthority('SCOPE_ROLE_CLIENTE') and @acessoCliente.mesmoCpf(#cpf))")
    public List<SolicitacaoResponse> listarSolicitacoes(@PathVariable String cpf) {
        return solicitacaoService.consultarPorCpf(cpf);
    }

    // Conta do cliente no Core Bancario; operador e admin consultam qualquer cliente
    @GetMapping("/{cpf}/conta")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR') or (hasAuthority('SCOPE_ROLE_CLIENTE') and @acessoCliente.mesmoCpf(#cpf))")
    public ContaClienteResponse consultarConta(@PathVariable String cpf) {
        return solicitacaoService.consultarContaCliente(cpf);
    }
}
