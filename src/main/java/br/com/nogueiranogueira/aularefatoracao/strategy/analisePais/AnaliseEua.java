package br.com.nogueiranogueira.aularefatoracao.strategy.analisePais;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;

public final class AnaliseEua implements AnalisePaisStrategy {

    @Override
    public boolean aprovar(SolicitacaoCredito solicitacao) {
        return solicitacao.getScore() > 700;
    }
}