package br.com.fiap.bank.plataforma.infrastructure.persistencia;

import br.com.fiap.bank.plataforma.domain.model.Perfil;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

// Mapeamento JPA da tabela tb_usuario, com os perfis na tabela tb_usuario_perfil
@Entity
@Table(name = "tb_usuario")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    private String login;

    @Column(name = "senha_hash", nullable = false)
    private String senhaHash;

    @Column(nullable = false)
    private String nome;

    @Column(length = 11)
    private String cpf;

    private String conta;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tb_usuario_perfil", joinColumns = @JoinColumn(name = "login"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false)
    private Set<Perfil> perfis = new HashSet<>();
}
