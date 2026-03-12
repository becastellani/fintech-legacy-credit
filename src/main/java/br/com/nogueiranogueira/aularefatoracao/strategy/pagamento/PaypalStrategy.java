package br.com.nogueiranogueira.aularefatoracao.strategy.pagamento;

public class PaypalStrategy implements PagamentoStrategy {

    @Override
    public void pagar(double valor) {
        System.out.println("Gerando token de sessão do PayPal...");
        System.out.println("Redirecionando cliente para a carteira digital.");
        System.out.println("Pagamento via PayPal processado. Total cobrado: R$ " + valor);
    }
}
