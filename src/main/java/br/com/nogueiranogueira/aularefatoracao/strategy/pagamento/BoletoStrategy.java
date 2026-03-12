package br.com.nogueiranogueira.aularefatoracao.strategy.pagamento;

public class BoletoStrategy implements PagamentoStrategy {

    private static final double TAXA_EMISSAO = 3.50;

    @Override
    public void pagar(double valor) {
        double valorBoleto = valor + TAXA_EMISSAO;
        System.out.println("Registrando boleto no banco emissor...");
        System.out.println("Gerando código de barras com vencimento para 3 dias úteis.");
        System.out.println("Pagamento via Boleto processado. Total cobrado: R$ " + valorBoleto);
    }
}
