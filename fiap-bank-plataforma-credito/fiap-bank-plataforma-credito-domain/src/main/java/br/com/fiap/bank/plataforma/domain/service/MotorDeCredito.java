package br.com.fiap.bank.plataforma.domain.service;

import br.com.fiap.bank.plataforma.domain.model.ResultadoAnalise;
import br.com.fiap.bank.plataforma.domain.model.SolicitacaoCredito;
import br.com.fiap.bank.plataforma.domain.model.StatusSolicitacao;

import java.math.BigDecimal;
import java.util.Locale;

// Regras automaticas de decisao de credito, sem dependencia de framework
public class MotorDeCredito {

    // Score do Bureau abaixo deste valor reprova direto
    private static final int SCORE_MINIMO = 500;

    // Parcela acima de 30% da renda vai para a Mesa de Operacoes
    private static final BigDecimal COMPROMETIMENTO_MAXIMO_DA_RENDA = new BigDecimal("0.30");

    // Aplica as regras de SCR, score, 30% da renda e CRM
    public ResultadoAnalise analisar(boolean restricaoScr, int scoreBureau, boolean inadimplenciaCrm,
                                     BigDecimal valor, int parcelas, BigDecimal renda) {
        if (restricaoScr) {
            return new ResultadoAnalise(StatusSolicitacao.REPROVADO,
                    "Solicitação reprovada automaticamente devido a restrições ativas no SCR (Banco Central).");
        }
        if (scoreBureau < SCORE_MINIMO) {
            return new ResultadoAnalise(StatusSolicitacao.REPROVADO,
                    "Solicitação reprovada automaticamente. Score no Bureau de Crédito insuficiente (" + scoreBureau + ").");
        }

        BigDecimal parcelaEstimada = SolicitacaoCredito.calcularParcela(valor, parcelas);
        if (parcelaEstimada.compareTo(renda.multiply(COMPROMETIMENTO_MAXIMO_DA_RENDA)) > 0) {
            return new ResultadoAnalise(StatusSolicitacao.EM_ANALISE,
                    "Encaminhado para a Mesa de Operações de Crédito. Parcela estimada (R$ " + formatarReais(parcelaEstimada)
                            + ") compromete mais de 30% da renda informada.");
        }
        if (inadimplenciaCrm) {
            return new ResultadoAnalise(StatusSolicitacao.EM_ANALISE,
                    "Encaminhado para a Mesa de Operações de Crédito. O histórico do cliente no CRM registra inadimplência anterior.");
        }
        return new ResultadoAnalise(StatusSolicitacao.APROVADO,
                "Solicitação aprovada automaticamente pelo Motor de Crédito FIAP Bank!");
    }

    // Valor em reais no formato brasileiro, com ponto no milhar e virgula nos centavos
    private static String formatarReais(BigDecimal valor) {
        return String.format(Locale.of("pt", "BR"), "%,.2f", valor);
    }
}
