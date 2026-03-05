package br.com.becastellani.aularefatoracao.strategy;

import br.com.becastellani.aularefatoracao.model.TipoConta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnaliseStrategyPJ implements AnaliseStrategy {

    private static final double VALOR_LIMITE_PJ         = 50_000.0;
    private static final int    SCORE_MINIMO_VALOR_ALTO = 700;

    @Override
    public boolean elegivel(TipoConta tipoConta) {
        return TipoConta.PJ == tipoConta;
    }

    @Override
    public boolean analisar(double valor, int score) {
        if (valor > VALOR_LIMITE_PJ && score < SCORE_MINIMO_VALOR_ALTO) {
            log.warn("Reprovado PJ: risco alto — valor R$ {} com score {}", valor, score);
            return false;
        }
        log.info("Aprovado PJ");
        return true;
    }
}

