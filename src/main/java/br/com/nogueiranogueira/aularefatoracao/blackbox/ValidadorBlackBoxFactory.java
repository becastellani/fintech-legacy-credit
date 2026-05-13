package br.com.nogueiranogueira.aularefatoracao.blackbox;

/**
 * Módulo Black-box — Fábrica de motores de validação.
 *
 * Centraliza a criação de {@link MotorValidacaoBlackBox} com as regras corretas,
 * desacoplando os clientes da lógica de seleção e instanciação.
 *
 * Em produção, este factory pode ser substituído pelo container de DI do Spring
 * ({@code @Bean} / {@code @Qualifier}). A interface {@link RegraValidacaoFinanceira}
 * permanece a mesma — apenas o ponto de montagem muda.
 */
public class ValidadorBlackBoxFactory {

    private ValidadorBlackBoxFactory() { }

    /**
     * Cria um motor configurado com a regra padrão de Crédito Digital.
     */
    public static MotorValidacaoBlackBox criarParaCreditoDigital() {
        return new MotorValidacaoBlackBox(new RegraValidacaoCreditoDigital());
    }

    /**
     * Cria um motor com uma regra customizada — útil para testes e cenários específicos.
     *
     * @param regra qualquer implementação de {@link RegraValidacaoFinanceira}
     */
    public static MotorValidacaoBlackBox criar(RegraValidacaoFinanceira regra) {
        return new MotorValidacaoBlackBox(regra);
    }
}
