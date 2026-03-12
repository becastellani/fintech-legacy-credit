package br.com.nogueiranogueira.aularefatoracao.model.constantes;

import lombok.Getter;

public enum RegiaoFrete {

    PARANA("85", 10.0),
    SAO_PAULO("01", 20.0),
    OUTROS("", 50.0);

    private final String prefixoCep;
    @Getter
    private final double valorFrete;

    RegiaoFrete(String prefixoCep, double valorFrete) {
        this.prefixoCep = prefixoCep;
        this.valorFrete = valorFrete;
    }

    /**
     * Resolve a região a partir do CEP informado.
     * Retorna OUTROS caso nenhum prefixo conhecido seja encontrado.
     */
    public static RegiaoFrete fromCep(String cep) {
        if (cep == null || cep.isBlank()) {
            return OUTROS;
        }
        for (RegiaoFrete regiao : values()) {
            if (!regiao.prefixoCep.isEmpty() && cep.startsWith(regiao.prefixoCep)) {
                return regiao;
            }
        }
        return OUTROS;
    }
}

