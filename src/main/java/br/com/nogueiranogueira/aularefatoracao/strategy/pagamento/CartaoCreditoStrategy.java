package br.com.nogueiranogueira.aularefatoracao.strategy.pagamento;

public class CartaoCreditoStrategy implements PagamentoStrategy {

    private static final double TAXA_CARTAO = 1.05;

    @Override
    public void pagar(double valor) {
        double valorComAcrescimo = valor * TAXA_CARTAO;
        System.out.println("Conectando com a adquirente (Cielo/Rede)...");
        System.out.println("Validando limite e risco de fraude.");
        System.out.println("Pagamento via Cartão processado. Total cobrado: R$ " + valorComAcrescimo);
    }
}
