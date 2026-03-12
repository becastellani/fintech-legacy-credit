package br.com.nogueiranogueira.aularefatoracao.factory;

import br.com.nogueiranogueira.aularefatoracao.model.constantes.TipoPagamento;
import br.com.nogueiranogueira.aularefatoracao.strategy.pagamento.BoletoStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.pagamento.CartaoCreditoStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.pagamento.PaypalStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.pagamento.PagamentoStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.pagamento.PixStrategy;

public class PagamentoFactory {

    private PagamentoFactory() {}

    public static PagamentoStrategy obterEstrategia(TipoPagamento tipo) {
        return switch (tipo) {
            case PIX            -> new PixStrategy();
            case CARTAO_CREDITO -> new CartaoCreditoStrategy();
            case PAYPAL         -> new PaypalStrategy();
            case BOLETO         -> new BoletoStrategy();
        };
    }
}
