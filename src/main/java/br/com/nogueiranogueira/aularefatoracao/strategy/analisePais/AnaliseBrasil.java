package br.com.nogueiranogueira.aularefatoracao.strategy.analisePais;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;

public final class AnaliseBrasil implements AnalisePaisStrategy {

    @Override
    public boolean aprovar(SolicitacaoCredito solicitacao) {
        // regra exemplo
        return solicitacao.getScore() > 600;
    }
}