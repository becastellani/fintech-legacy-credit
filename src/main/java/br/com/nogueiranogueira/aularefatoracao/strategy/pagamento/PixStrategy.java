package br.com.nogueiranogueira.aularefatoracao.strategy.pagamento;

public class PixStrategy implements PagamentoStrategy {

    private static final double DESCONTO_PIX = 0.95;

    @Override
    public void pagar(double valor) {
        double valorComDesconto = valor * DESCONTO_PIX;
        System.out.println("Calculando desconto do PIX...");
        System.out.println("Gerando chave Copia e Cola.");
        System.out.println("Pagamento via PIX processado. Total cobrado: R$ " + valorComDesconto);
    }
}
