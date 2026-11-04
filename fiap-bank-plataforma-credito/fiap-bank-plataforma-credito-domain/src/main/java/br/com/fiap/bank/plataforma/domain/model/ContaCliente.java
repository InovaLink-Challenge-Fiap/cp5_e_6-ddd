package br.com.fiap.bank.plataforma.domain.model;

import br.com.fiap.bank.plataforma.domain.excecao.ContaNaoAtivaException;

import java.math.BigDecimal;

// Dados da conta do cliente vindos do Core Bancario
public record ContaCliente(
    String statusConta,
    BigDecimal saldo,
    String agencia,
    String numeroConta
) {
    // Diz se a conta esta ativa e pode receber o credito
    public boolean ativa() {
        return "ATIVA".equals(statusConta);
    }

    // Regra de concessao: conta que nao esta ativa nao pede credito
    public void exigirAtiva() {
        if (!ativa()) {
            throw new ContaNaoAtivaException(statusConta);
        }
    }
}
