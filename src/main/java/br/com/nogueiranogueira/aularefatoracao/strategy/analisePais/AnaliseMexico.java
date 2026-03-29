package br.com.nogueiranogueira.aularefatoracao.strategy.analisePais;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;

public final class AnaliseMexico implements AnalisePaisStrategy {

    @Override
    public boolean aprovar(SolicitacaoCredito solicitacao) {
        return solicitacao.getScore() > 650;
    }
}