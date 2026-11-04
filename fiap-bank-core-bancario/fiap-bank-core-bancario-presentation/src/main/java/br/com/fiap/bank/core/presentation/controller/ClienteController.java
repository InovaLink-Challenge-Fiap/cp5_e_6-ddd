package br.com.fiap.bank.core.presentation.controller;

import br.com.fiap.bank.core.application.dto.ClienteResponse;
import br.com.fiap.bank.core.application.dto.CriarClienteRequest;
import br.com.fiap.bank.core.application.service.ClienteService;
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
import java.util.UUID;

// API REST de clientes em /api/v1/clientes
@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // Lista todos os clientes com 200
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(clienteService.listar());
    }

    // Busca o cliente pelo UUID e devolve 404 se nao existir
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    // Busca o cliente pelo CPF; 400 se o CPF for invalido e 404 se nao existir
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClienteResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(cpf));
    }

    // Cadastra o cliente e devolve 201 com o Location apontando para o id novo
    @PostMapping
    public ResponseEntity<ClienteResponse> criar(@Valid @RequestBody CriarClienteRequest request) {
        ClienteResponse cliente = clienteService.criar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(cliente);
    }

    // Atualiza todos os dados do cliente usando o mesmo corpo do POST
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> atualizar(@PathVariable UUID id, @Valid @RequestBody CriarClienteRequest request) {
        return ResponseEntity.ok(clienteService.atualizar(id, request));
    }

    // Apaga o cliente e a conta dele e devolve 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        clienteService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
