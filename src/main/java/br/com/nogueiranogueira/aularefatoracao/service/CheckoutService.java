package br.com.nogueiranogueira.aularefatoracao.service;

public class CheckoutService {

    /**
     * BAD SMELLS PRESENTES NESTE CÓDIGO:
     * 1. Violação do OCP (Open/Closed Principle): Para adicionar "CRIPTO", precisamos modificar este método.
     * 2. Violação do SRP (Single Responsibility Principle): A classe valida, calcula descontos/taxas e simula integrações.
     * 3. Magic Numbers: O que é 0.95? O que é 1.05? (Deveriam ser constantes).
     * 4. Código Duplicado: A estrutura de log e impressão se repete muito.
     */
    public void pagar(double valor, String metodo) {

        System.out.println("=== Iniciando processamento de pagamento ===");

        if (metodo == null || metodo.trim().isEmpty()) {
            throw new IllegalArgumentException("Método de pagamento não informado!");
        }

        if (metodo.equalsIgnoreCase("PIX")) {
            // Regra do PIX: 5% de desconto
            double valorComDesconto = valor * 0.95;
            System.out.println("Calculando desconto do PIX...");
            System.out.println("Gerando chave Copia e Cola.");
            System.out.println("Pagamento via PIX processado. Total cobrado: R$ " + valorComDesconto);

        } else if (metodo.equalsIgnoreCase("CARTAO_CREDITO")) {
            // Regra do Cartão: 5% de taxa de conveniência/juros
            double valorComAcrescimo = valor * 1.05;
            System.out.println("Conectando com a adquirente (Cielo/Rede)...");
            System.out.println("Validando limite e risco de fraude.");
            System.out.println("Pagamento via Cartão processado. Total cobrado: R$ " + valorComAcrescimo);

        } else if (metodo.equalsIgnoreCase("PAYPAL")) {
            // Regra PayPal: Sem taxa extra, mas com redirecionamento
            System.out.println("Gerando token de sessão do PayPal...");
            System.out.println("Redirecionando cliente para a carteira digital.");
            System.out.println("Pagamento via PayPal processado. Total cobrado: R$ " + valor);

        } else if (metodo.equalsIgnoreCase("BOLETO")) {
            // Regra Boleto: Taxa fixa de R$ 3,50 para emissão
            double valorBoleto = valor + 3.50;
            System.out.println("Registrando boleto no banco emissor...");
            System.out.println("Gerando código de barras com vencimento para 3 dias úteis.");
            System.out.println("Pagamento via Boleto processado. Total cobrado: R$ " + valorBoleto);

        } else {
            // O pesadelo da manutenção: Qualquer erro de digitação cai aqui.
            throw new IllegalArgumentException("Método de pagamento não suportado: " + metodo);
        }

        System.out.println("=== Finalizando transação ===");
    }
}