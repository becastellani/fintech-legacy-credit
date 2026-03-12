package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.model.constantes.RegiaoFrete;
import br.com.nogueiranogueira.aularefatoracao.model.constantes.TipoProduto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProcessadorVendaService {

    public void processar(String cliente, double valor, String tipo, String cep) {
        if (!validarEntrada(cliente, valor)) {
            return;
        }

        double frete   = calcularFrete(cep);
        double imposto = calcularImposto(valor, tipo);
        double total   = valor + frete + imposto;

        persistirPedido(cliente, total);
        log.info("Recibo enviado para {}", cliente);
    }

    private boolean validarEntrada(String cliente, double valor) {
        if (cliente == null || cliente.isBlank()) {
            log.error("Erro: Cliente inválido");
            return false;
        }
        if (valor <= 0) {
            log.error("Erro: Valor inválido ({})", valor);
            return false;
        }
        return true;
    }

    private double calcularFrete(String cep) {
        double frete = RegiaoFrete.fromCep(cep).getValorFrete();
        log.debug("Frete calculado para CEP {}: R$ {}", cep, frete);
        return frete;
    }

    private double calcularImposto(double valor, String tipo) {
        try {
            TipoProduto tipoProduto = TipoProduto.fromString(tipo);
            double imposto = tipoProduto.calcularImposto(valor);
            log.debug("Imposto calculado para {}: R$ {}", tipo, imposto);
            return imposto;
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de produto desconhecido '{}', imposto zerado", tipo);
            return 0.0;
        }
    }

    private void persistirPedido(String cliente, double total) {
        log.info("Persistindo pedido — cliente: {}, total: R$ {}", cliente, total);
        log.debug("INSERT INTO PEDIDOS VALUES ({}, {})", cliente, total);
    }
}
