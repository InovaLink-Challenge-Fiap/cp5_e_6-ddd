package br.com.fiap.bank.plataforma.application.config;

import br.com.fiap.bank.plataforma.application.dto.SolicitarCreditoRequest;
import br.com.fiap.bank.plataforma.application.seguranca.CodificadorSenha;
import br.com.fiap.bank.plataforma.application.service.SolicitacaoCreditoService;
import br.com.fiap.bank.plataforma.domain.model.Cpf;
import br.com.fiap.bank.plataforma.domain.model.Perfil;
import br.com.fiap.bank.plataforma.domain.model.Usuario;
import br.com.fiap.bank.plataforma.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

// Carga inicial dos usuarios e das solicitacoes de exemplo ao subir a aplicacao
@Slf4j
@Component
@RequiredArgsConstructor
public class PlataformaDataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final CodificadorSenha codificadorSenha;
    private final SolicitacaoCreditoService solicitacaoService;

    // Carrega os usuarios antes das solicitacoes
    @Override
    public void run(String... args) {
        carregarUsuarios();
        carregarSolicitacoes();
    }

    // Cria os quatro clientes, o operador e o admin
    private void carregarUsuarios() {
        criarUsuario("12345678900", "123456", "Ana Silva", new Cpf("12345678900"), "12345-6", Perfil.CLIENTE);
        criarUsuario("98765432100", "123456", "Carlos Eduardo Oliveira", new Cpf("98765432100"), "78901-2", Perfil.CLIENTE);
        criarUsuario("55544433399", "123456", "Mariana Souza", new Cpf("55544433399"), "33333-3", Perfil.CLIENTE);
        criarUsuario("11122233347", "123456", "Bruno Ferreira", new Cpf("11122233347"), "44444-7", Perfil.CLIENTE);
        criarUsuario("operador", "operador123", "Carlos Ramos", null, null, Perfil.OPERADOR);
        criarUsuario("admin", "admin123", "Administrador", null, null, Perfil.ADMIN);
    }

    // Grava um usuario com a senha ja codificada e um unico perfil
    private void criarUsuario(String login, String senha, String nome, Cpf cpf, String conta, Perfil perfil) {
        usuarioRepository.salvar(new Usuario(login, codificadorSenha.codificar(senha), nome, cpf, conta, Set.of(perfil)));
    }

    // Cria as cinco solicitacoes de exemplo pelo mesmo fluxo de uma solicitacao real
    private void carregarSolicitacoes() {
        criarSolicitacao("12345678900", "Ana Silva", "EMPRÉSTIMO PESSOAL", "15000.00", 24, "7500.00");
        criarSolicitacao("98765432100", "Carlos Eduardo Oliveira", "CRÉDITO IMOBILIÁRIO", "350000.00", 180, "12000.00");
        criarSolicitacao("55544433399", "Mariana Souza", "CRÉDITO AUTOMOTIVO", "45000.00", 48, "4200.00");
        criarSolicitacao("12345678900", "Ana Silva", "CRÉDITO AUTOMOTIVO", "80000.00", 12, "7500.00");
        criarSolicitacao("11122233347", "Bruno Ferreira", "EMPRÉSTIMO PESSOAL", "6000.00", 12, "5000.00");
    }

    // Cada solicitacao falha sozinha para a aplicacao subir mesmo com o Core fora do ar
    private void criarSolicitacao(String cpf, String nome, String tipo, String valor, int parcelas, String renda) {
        try {
            solicitacaoService.solicitar(new SolicitarCreditoRequest(cpf, nome, tipo,
                    new BigDecimal(valor), parcelas, new BigDecimal(renda)));
        } catch (RuntimeException e) {
            log.warn("Solicitacao inicial de {} nao carregada: {}", nome, e.getClass().getSimpleName());
        }
    }
}
