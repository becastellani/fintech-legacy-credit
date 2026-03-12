package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.factory.PagamentoFactory;
import br.com.nogueiranogueira.aularefatoracao.model.constantes.TipoPagamento;
import br.com.nogueiranogueira.aularefatoracao.strategy.pagamento.PagamentoStrategy;

public class CheckoutService {

    public void pagar(double valor, String metodo) {
        System.out.println("=== Iniciando processamento de pagamento ===");

        TipoPagamento tipo = TipoPagamento.fromString(metodo);
        PagamentoStrategy strategy = PagamentoFactory.obterEstrategia(tipo);
        strategy.pagar(valor);

        System.out.println("=== Finalizando transação ===");
    }
}
