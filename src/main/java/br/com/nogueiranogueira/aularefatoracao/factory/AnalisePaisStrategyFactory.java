package br.com.nogueiranogueira.aularefatoracao.factory;

import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnaliseBrasil;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnaliseEua;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnaliseMexico;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnalisePaisStrategy;


public class AnalisePaisStrategyFactory {

    public static AnalisePaisStrategy criar(String pais) {
        return switch (pais.toUpperCase()) {
            case "BR" -> new AnaliseBrasil();
            case "US" -> new AnaliseEua();
            case "MX" -> new AnaliseMexico();
            default -> throw new IllegalArgumentException("País não suportado");
        };
    }
}