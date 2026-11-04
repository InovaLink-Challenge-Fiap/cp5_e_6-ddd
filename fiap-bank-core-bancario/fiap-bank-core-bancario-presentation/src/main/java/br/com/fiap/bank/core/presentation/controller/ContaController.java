package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.AtualizarSaldoRequest;
import br.com.fiap.bank.core.application.dto.ContaResponse;
import br.com.fiap.bank.core.application.dto.CriarContaRequest;
import br.com.fiap.bank.core.application.service.ContaBancariaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// API REST de contas bancarias em /api/v1/contas
@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaBancariaService contaService;

    // Lista todas as contas com 200
    @GetMapping
    public ResponseEntity<List<ContaResponse>> listar() {
        return ResponseEntity.ok(contaService.listar());
    }

    // Busca a conta pelo numero; 400 se o numero for invalido e 404 se nao existir
    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaResponse> buscarPorNumero(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarPorNumero(numeroConta));
    }

    // Busca a conta pelo CPF do cliente; 400 se o CPF for invalido e 404 se nao houver conta
    @GetMapping("/cliente/{cpf}")
    public ResponseEntity<ContaResponse> buscarPorCpfCliente(@PathVariable String cpf) {
        return ResponseEntity.ok(contaService.buscarPorCpfCliente(cpf));
    }

    // Abre a conta e devolve 201 com o Location apontando para o numero da conta
    @PostMapping
    public ResponseEntity<ContaResponse> criar(@Valid @RequestBody CriarContaRequest request) {
        ContaResponse conta = contaService.criar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{numeroConta}")
                .buildAndExpand(conta.numeroConta())
                .toUri();
        return ResponseEntity.created(localizacao).body(conta);
    }

    // Deposito ou saque no saldo; saque acima do saldo ou conta encerrada devolve 409
    @PutMapping("/{numeroConta}/saldo")
    public ResponseEntity<ContaResponse> atualizarSaldo(@PathVariable String numeroConta, @Valid @RequestBody AtualizarSaldoRequest request) {
        return ResponseEntity.ok(contaService.atualizarSaldo(numeroConta, request));
    }

    // Marca a conta como ENCERRADA e devolve 204; 400 se o numero for invalido
    @DeleteMapping("/{numeroConta}")
    public ResponseEntity<Void> encerrar(@PathVariable String numeroConta) {
        contaService.encerrar(numeroConta);
        return ResponseEntity.noContent().build();
    }
}
