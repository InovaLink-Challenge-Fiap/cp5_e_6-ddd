package br.com.fiap.bank.plataforma.infrastructure.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio Spring Data da tabela de usuarios; a chave e o login
public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, String> {
}
