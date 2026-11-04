package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.excecao.DominioException;
import br.com.fiap.bank.plataforma.domain.excecao.TransicaoDeStatusInvalidaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

// Agregado da solicitacao de credito; status e parecer so mudam por aprovar e reprovar
public class SolicitacaoCredito {

    private final Long id;
    private final Cpf cpfCliente;
    private final String nomeCliente;
    private final String tipoCredito;
    private final BigDecimal valorSolicitado;
    private final Integer quantidadeParcelas;
    private final BigDecimal rendaMensal;
    private final Integer scoreCredito;
    private StatusSolicitacao status;
    private String parecerOperador;
    private final Boolean scrRestricoes;
    private final Integer scoreBureau;
    private final LocalDateTime dataSolicitacao;
    private LocalDateTime dataAtualizacao;

    // Construtor unico, usado por nova e por reconstituir
    private SolicitacaoCredito(Long id, Cpf cpfCliente, String nomeCliente, String tipoCredito,
                               BigDecimal valorSolicitado, Integer quantidadeParcelas, BigDecimal rendaMensal,
                               Integer scoreCredito, StatusSolicitacao status, String parecerOperador,
                               Boolean scrRestricoes, Integer scoreBureau,
                               LocalDateTime dataSolicitacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.cpfCliente = cpfCliente;
        this.nomeCliente = nomeCliente;
        this.tipoCredito = tipoCredito;
        this.valorSolicitado = valorSolicitado;
        this.quantidadeParcelas = quantidadeParcelas;
        this.rendaMensal = rendaMensal;
        this.scoreCredito = scoreCredito;
        this.status = status;
        this.parecerOperador = parecerOperador;
        this.scrRestricoes = scrRestricoes;
        this.scoreBureau = scoreBureau;
        this.dataSolicitacao = dataSolicitacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    // Cria uma solicitacao nova, validando os dados e aplicando o resultado do Motor de Credito
    public static SolicitacaoCredito nova(Cpf cpfCliente, String nomeCliente, String tipoCredito,
                                          BigDecimal valorSolicitado, int quantidadeParcelas, BigDecimal rendaMensal,
                                          boolean scrRestricoes, int scoreBureau, ResultadoAnalise resultado) {
        if (cpfCliente == null) {
            throw new DominioException("CPF do cliente e obrigatorio");
        }
        exigirTexto(nomeCliente, "Nome do cliente e obrigatorio");
        exigirTexto(tipoCredito, "Tipo de credito e obrigatorio");
        exigirPositivo(valorSolicitado, "Valor solicitado deve ser maior que zero");
        exigirPositivo(rendaMensal, "Renda mensal deve ser maior que zero");
        if (quantidadeParcelas <= 0) {
            throw new DominioException("Quantidade de parcelas deve ser maior que zero");
        }

        LocalDateTime agora = LocalDateTime.now();
        return new SolicitacaoCredito(null, cpfCliente, nomeCliente, tipoCredito, valorSolicitado,
                quantidadeParcelas, rendaMensal, scoreBureau, resultado.status(), resultado.parecer(),
                scrRestricoes, scoreBureau, agora, agora);
    }

    // Recria o agregado a partir dos dados ja persistidos, sem validar de novo
    public static SolicitacaoCredito reconstituir(Long id, Cpf cpfCliente, String nomeCliente, String tipoCredito,
                                                  BigDecimal valorSolicitado, Integer quantidadeParcelas,
                                                  BigDecimal rendaMensal, Integer scoreCredito,
                                                  StatusSolicitacao status, String parecerOperador,
                                                  Boolean scrRestricoes, Integer scoreBureau,
                                                  LocalDateTime dataSolicitacao, LocalDateTime dataAtualizacao) {
        return new SolicitacaoCredito(id, cpfCliente, nomeCliente, tipoCredito, valorSolicitado,
                quantidadeParcelas, rendaMensal, scoreCredito, status, parecerOperador,
                scrRestricoes, scoreBureau, dataSolicitacao, dataAtualizacao);
    }

    // Aprova pela Mesa de Operacoes; so sai de EM_ANALISE e usa OK quando o parecer vem nulo
    public void aprovar(String parecer) {
        exigirEmAnalise(StatusSolicitacao.APROVADO);
        this.status = StatusSolicitacao.APROVADO;
        this.parecerOperador = "Aprovado pela Mesa de Operações: " + (parecer != null ? parecer : "OK");
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Reprova pela Mesa de Operacoes; so sai de EM_ANALISE e tem motivo padrao quando vem nulo
    public void reprovar(String motivo) {
        exigirEmAnalise(StatusSolicitacao.REPROVADO);
        this.status = StatusSolicitacao.REPROVADO;
        this.parecerOperador = "Reprovado pela Mesa de Operações. Motivo: "
                + (motivo != null ? motivo : "Inconformidade nos critérios de risco");
        this.dataAtualizacao = LocalDateTime.now();
    }

    // Divide o valor pelo numero de parcelas, com duas casas e arredondamento HALF_UP
    public static BigDecimal calcularParcela(BigDecimal valor, int parcelas) {
        return valor.divide(BigDecimal.valueOf(parcelas), 2, RoundingMode.HALF_UP);
    }

    // Barra a mudanca de status quando a solicitacao nao esta mais em analise
    private void exigirEmAnalise(StatusSolicitacao destino) {
        if (status != StatusSolicitacao.EM_ANALISE) {
            throw new TransicaoDeStatusInvalidaException(status, destino);
        }
    }

    // Lanca DominioException com a mensagem quando o texto vem nulo ou em branco
    private static void exigirTexto(String valor, String mensagem) {
        if (valor == null || valor.isBlank()) {
            throw new DominioException(mensagem);
        }
    }

    // Lanca DominioException com a mensagem quando o valor vem nulo, zero ou negativo
    private static void exigirPositivo(BigDecimal valor, String mensagem) {
        if (valor == null || valor.signum() <= 0) {
            throw new DominioException(mensagem);
        }
    }

    public Long getId() { return id; } // Identificador, nulo antes de gravar
    public Cpf getCpfCliente() { return cpfCliente; } // CPF do solicitante
    public String getNomeCliente() { return nomeCliente; } // Nome do solicitante
    public String getTipoCredito() { return tipoCredito; } // Tipo de credito pedido
    public BigDecimal getValorSolicitado() { return valorSolicitado; } // Valor pedido
    public Integer getQuantidadeParcelas() { return quantidadeParcelas; } // Numero de parcelas
    public BigDecimal getRendaMensal() { return rendaMensal; } // Renda informada na solicitacao
    public Integer getScoreCredito() { return scoreCredito; } // Score usado na analise
    public StatusSolicitacao getStatus() { return status; } // Status atual
    public String getParecerOperador() { return parecerOperador; } // Parecer do motor ou da Mesa
    public Boolean getScrRestricoes() { return scrRestricoes; } // Restricao encontrada no SCR
    public Integer getScoreBureau() { return scoreBureau; } // Score consultado no Bureau
    public LocalDateTime getDataSolicitacao() { return dataSolicitacao; } // Quando foi criada
    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; } // Ultima mudanca de status

    // Duas solicitacoes sao a mesma quando tem o mesmo id ja gravado
    @Override
    public boolean equals(Object outro) {
        if (this == outro) {
            return true;
        }
        if (!(outro instanceof SolicitacaoCredito solicitacao)) {
            return false;
        }
        return id != null && id.equals(solicitacao.id);
    }

    // Hash calculado so pelo id, igual ao criterio do equals
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
