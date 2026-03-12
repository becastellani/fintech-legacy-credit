package br.com.nogueiranogueira.aularefatoracao.model.constantes;

public enum TipoProduto {

    PRODUTO(0.18),
    SERVICO(0.05);

    private final double aliquotaImposto;

    TipoProduto(double aliquotaImposto) {
        this.aliquotaImposto = aliquotaImposto;
    }

    public double calcularImposto(double valor) {
        return valor * aliquotaImposto;
    }

    public static TipoProduto fromString(String valor) {
        for (TipoProduto tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de produto inválido: " + valor);
    }
}

