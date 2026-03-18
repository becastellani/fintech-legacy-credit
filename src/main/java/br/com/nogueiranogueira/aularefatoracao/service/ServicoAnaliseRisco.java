package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;

public interface ServicoAnaliseRisco {
    boolean avaliarCredito(SolicitacaoCredito solicitacao);
}