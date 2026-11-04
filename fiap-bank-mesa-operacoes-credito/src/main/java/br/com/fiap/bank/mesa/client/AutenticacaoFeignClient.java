package br.com.fiap.bank.mesa.client;

import br.com.fiap.bank.mesa.dto.LoginRequestDTO;
import br.com.fiap.bank.mesa.dto.LoginResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// Cliente Feign da rota de login da Plataforma de Credito
@FeignClient(name = "plataformaAutenticacaoClient", url = "${plataforma.credito.url:http://localhost:8081}")
public interface AutenticacaoFeignClient {

    // Troca usuario e senha pelo JWT e pelos dados do usuario
    @PostMapping("/api/auth/login")
    LoginResponseDTO autenticar(@RequestBody LoginRequestDTO request);
}
