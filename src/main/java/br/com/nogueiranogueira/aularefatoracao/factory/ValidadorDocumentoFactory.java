package br.com.nogueiranogueira.aularefatoracao.factory;

import br.com.nogueiranogueira.aularefatoracao.strategy.documento.CnpjValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.CpfValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.ValidadorDocumentoStrategy;

public class ValidadorDocumentoFactory {
    private ValidadorDocumentoFactory() { }

    public static ValidadorDocumentoStrategy obter(String documento) {
        String limpo = documento.replaceAll("[^0-9]", "");
        return switch (limpo.length()){
            case 11 -> new CpfValidadorStrategy();
            case 10 -> new CnpjValidadorStrategy();
            default -> throw new IllegalArgumentException("Verificar documento digitado:" + documento);
        };
    }
}
