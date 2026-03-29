package br.com.nogueiranogueira.aularefatoracao.factory;

import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnaliseBrasil;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnaliseEua;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnaliseMexico;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnalisePortugal;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnalisePaisStrategy;

/**
 * LPS — Fábrica de Variantes para o ponto de variação "AnalisePaisStrategy".
 *
 * Mercados suportados: BR, US, MX, PT.
 * Adicionar um novo mercado = criar nova variante final + registrar um case aqui.
 * Nenhuma outra classe precisa ser alterada.
 */
public class AnalisePaisStrategyFactory {

    public static AnalisePaisStrategy criar(String pais) {
        return switch (pais.toUpperCase()) {
            case "BR" -> new AnaliseBrasil();
            case "US" -> new AnaliseEua();
            case "MX" -> new AnaliseMexico();
            case "PT" -> new AnalisePortugal();
            default -> throw new IllegalArgumentException("País não suportado: " + pais);
        };
    }
}