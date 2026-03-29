package br.com.nogueiranogueira.aularefatoracao.strategy.analisePais;
import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;

public sealed interface AnalisePaisStrategy permits AnaliseBrasil, AnaliseEua, AnaliseMexico {

    boolean aprovar(SolicitacaoCredito solicitacao);
}
