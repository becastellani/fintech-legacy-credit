package br.com.nogueiranogueira.aularefatoracao.strategy.analisePais;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;

/**
 * LPS — Variante: regras de aprovação de crédito para Portugal.
 *
 * Limiar de score definido conforme regulação do Banco de Portugal:
 * score mínimo de 650 para qualquer modalidade.
 */
public final class AnalisePortugal implements AnalisePaisStrategy {

    @Override
    public boolean aprovar(SolicitacaoCredito solicitacao) {
        return solicitacao.getScore() > 650;
    }
}
