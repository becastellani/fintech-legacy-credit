package br.com.becastellani.aularefatoracao.strategy;

import br.com.becastellani.aularefatoracao.model.TipoConta;

public interface AnaliseStrategy {

    /**
     * Verifica se esta strategy é elegível para o tipo de conta informado.
     */
    boolean elegivel(TipoConta tipoConta);

    /**
     * Executa a análise de crédito conforme as regras do tipo de conta.
     *
     * @return true se aprovado, false se reprovado
     */
    boolean analisar(double valor, int score);
}

