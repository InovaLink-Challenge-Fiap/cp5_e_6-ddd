package br.com.fiap.bank.plataforma.presentation.controller;

import br.com.fiap.bank.plataforma.application.dto.SolicitacaoResponse;
import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.dto.StatusUpdateRequest;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

// API v1 das solicitacoes de credito
@RestController
@RequestMapping("/api/v1/solicitacoes")
@RequiredArgsConstructor
public class SolicitacaoCreditoRestController {

    private final SolicitacaoCreditoService solicitacaoService;

    // Cria a proposta do proprio cliente e responde 201 com o Location da solicitacao criada
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_CLIENTE') and @acessoCliente.mesmoCpf(#request.cpfCliente())")
    public ResponseEntity<SolicitacaoResponse> criarProposta(@Valid @RequestBody SolicitarCreditoRequest request) {
        SolicitacaoResponse criada = solicitacaoService.solicitar(request);
        URI localizacao = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.id())
                .toUri();
        return ResponseEntity.created(localizacao).body(criada);
    }

    // Lista todas as solicitacoes; so operador e admin
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public List<SolicitacaoResponse> listarTodas() {
        return solicitacaoService.listarTodas();
    }

    // Busca uma solicitacao pelo id; so operador e admin
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public SolicitacaoResponse buscarPorId(@PathVariable Long id) {
        return solicitacaoService.buscarPorId(id);
    }

    // Aprova ou reprova uma solicitacao em analise e responde 204 sem corpo; so operador e admin
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERADOR')")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id,
                                                @Valid @RequestBody StatusUpdateRequest request) {
        solicitacaoService.atualizarStatus(id, request.status(), request.parecerOperador());
        return ResponseEntity.noContent().build();
    }
}
