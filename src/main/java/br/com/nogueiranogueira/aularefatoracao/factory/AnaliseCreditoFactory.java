package br.com.nogueiranogueira.aularefatoracao.factory;

import br.com.nogueiranogueira.aularefatoracao.model.TipoConta;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategyPF;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategyPJ;

/**
 * Factory responsável por instanciar a estratégia de análise de crédito
 * correta conforme o TipoConta informado.
 *
 * Centraliza todos os 'new' das strategies, desacoplando a lógica de criação
 * das classes de negócio (AnaliseCreditoService, ProcessadorCreditoService).
 *
 * Java 21: Switch Expression com arrow syntax (->).
 * O compilador obriga a cobrir todos os casos do enum — se um novo TipoConta
 * for adicionado sem atualizar aqui, o código não compila.
 */
public class AnaliseCreditoFactory {

    private AnaliseCreditoFactory() {
    }

    /**
     * Retorna a estratégia elegível para o tipo de conta informado.
     * Sempre retorna a abstração (AnaliseStrategy), nunca a classe concreta.
     *
     * @param tipo TipoConta (PF ou PJ)
     * @return instância da strategy correspondente
     * @throws IllegalArgumentException se o tipo não for suportado
     */
    public static AnaliseStrategy obterEstrategia(TipoConta tipo) {
        return switch (tipo) {
            case PF -> new AnaliseStrategyPF();
            case PJ -> new AnaliseStrategyPJ();
        };
    }
}
