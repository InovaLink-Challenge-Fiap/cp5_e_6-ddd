package br.com.fiap.bank.core.infrastructure.persistencia;

import br.com.fiap.bank.core.domain.objetovalor.StatusConta;
import br.com.fiap.bank.core.domain.objetovalor.TipoConta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Linha da tabela tb_conta_bancaria; a ordem dos campos define o construtor usado pelo MapeadorPersistencia
@Entity
@Table(name = "tb_conta_bancaria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ContaBancariaEntity {

    @Id
    private UUID id;

    @Column(name = "numero_conta", nullable = false, unique = true, length = 7)
    private String numeroConta;

    @Column(nullable = false, length = 4)
    private String agencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false)
    private TipoConta tipoConta;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta", nullable = false)
    private StatusConta statusConta;

    @Column(name = "data_abertura", nullable = false)
    private LocalDate dataAbertura;
}
