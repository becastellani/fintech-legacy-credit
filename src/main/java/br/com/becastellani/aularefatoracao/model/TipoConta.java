package br.com.becastellani.aularefatoracao.model;

public enum TipoConta {
    PF,
    PJ;

    public static TipoConta fromString(String valor) {
        for (TipoConta tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de conta inválido: " + valor);
    }
}

