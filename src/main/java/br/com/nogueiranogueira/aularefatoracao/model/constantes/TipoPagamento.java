package br.com.nogueiranogueira.aularefatoracao.model.constantes;

public enum TipoPagamento {
    PIX,
    CARTAO_CREDITO,
    PAYPAL,
    BOLETO;

    public static TipoPagamento fromString(String valor) {
        for (TipoPagamento tipo : values()) {
            if (tipo.name().equalsIgnoreCase(valor)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Método de pagamento não suportado: " + valor);
    }
}
