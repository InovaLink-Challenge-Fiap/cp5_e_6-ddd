package br.com.fiap.bank.plataforma.application.dto;

import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Resposta com os dados de uma solicitacao de credito, usada na API v1 e no adaptador legado
public record SolicitacaoResponse(
    Long id,
    String cpfCliente,
    String nomeCliente,
    String tipoCredito,
    BigDecimal valorSolicitado,
    Integer quantidadeParcelas,
    BigDecimal rendaMensal,
    Integer scoreCredito,
    String status,
    String parecerOperador,
    Boolean scrRestricoes,
    Integer scoreBureau,
    LocalDateTime dataSolicitacao,
    LocalDateTime dataAtualizacao
) {
    // Monta a resposta a partir do agregado, com o CPF so em digitos
    public static SolicitacaoResponse de(SolicitacaoCredito solicitacao) {
        return new SolicitacaoResponse(
            solicitacao.getId(),
            solicitacao.getCpfCliente().valor(),
            solicitacao.getNomeCliente(),
            solicitacao.getTipoCredito(),
            solicitacao.getValorSolicitado(),
            solicitacao.getQuantidadeParcelas(),
            solicitacao.getRendaMensal(),
            solicitacao.getScoreCredito(),
            solicitacao.getStatus().name(),
            solicitacao.getParecerOperador(),
            solicitacao.getScrRestricoes(),
            solicitacao.getScoreBureau(),
            solicitacao.getDataSolicitacao(),
            solicitacao.getDataAtualizacao()
        );
    }
}
