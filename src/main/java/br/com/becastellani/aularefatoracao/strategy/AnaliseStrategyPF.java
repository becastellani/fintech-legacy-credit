package br.com.becastellani.aularefatoracao.strategy;

import br.com.becastellani.aularefatoracao.model.TipoConta;
import br.com.becastellani.aularefatoracao.model.constantes.DiaSemana;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AnaliseStrategyPF implements AnaliseStrategy {

    private static final double VALOR_LIMITE_PF          = 5_000.0;
    private static final int    SCORE_MINIMO_VALOR_ALTO  = 800;

    @Override
    public boolean elegivel(TipoConta tipoConta) {
        return TipoConta.PF == tipoConta;
    }

    @Override
    public boolean analisar(double valor, int score) {
        if (valor > VALOR_LIMITE_PF && score < SCORE_MINIMO_VALOR_ALTO) {
            log.warn("Reprovado PF: valor alto (R$ {}) com score médio ({})", valor, score);
            return false;
        }
        if (DiaSemana.isFinaldeSemana()) {
            log.warn("Reprovado PF: aprovação manual necessária no fim de semana");
            return false;
        }
        log.info("Aprovado PF");
        return true;
    }
}

