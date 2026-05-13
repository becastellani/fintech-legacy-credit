package br.com.nogueiranogueira.aularefatoracao.blackbox;

import org.becastellani.validador.documento.Documento;

/**
 * Módulo Black-box — Motor de execução por composição e Injeção de Dependência.
 *
 * Recebe qualquer implementação de {@link RegraValidacaoFinanceira} via construtor,
 * sem saber qual produto está sendo validado. Isso é Inversão de Controle (IoC) via
 * composição: o motor não instancia a regra — ela é injetada de fora.
 *
 * Princípios aplicados:
 *  - Dependency Inversion Principle (DIP): depende da abstração, não da implementação
 *  - Single Responsibility: o motor apenas orquestra log + execução, sem lógica de negócio
 *  - Open/Closed: novos produtos são adicionados implementando {@link RegraValidacaoFinanceira},
 *    sem alterar esta classe
 *
 * A IoC ocorre na linha:
 * {@code private final RegraValidacaoFinanceira regra; // injetada via construtor}
 * — o controle sobre QUAL regra executar pertence ao chamador, não ao motor.
 */
public class MotorValidacaoBlackBox {

    // ← IoC: a dependência é injetada pelo chamador; o motor não escolhe a implementação
    private final RegraValidacaoFinanceira regra;

    /**
     * Injeção de Dependência via construtor (Constructor Injection).
     * Favorece imutabilidade e testabilidade — basta passar um mock no teste.
     *
     * @param regra implementação concreta da regra financeira a ser executada
     */
    public MotorValidacaoBlackBox(RegraValidacaoFinanceira regra) {
        this.regra = regra;
    }

    /**
     * Executa o fluxo: log → delega para a regra injetada → retorna resultado.
     *
     * @param documento documento de identidade do cliente
     * @param valor     valor da operação financeira
     * @return true se aprovado pela regra injetada
     */
    public boolean executar(Documento documento, double valor) {
        System.out.println("[BLACKBOX][MOTOR] Executando regra: " + regra.descricao());
        boolean resultado = regra.validar(documento, valor);
        System.out.println("[BLACKBOX][MOTOR] Resultado: " + (resultado ? "APROVADO" : "REPROVADO"));
        return resultado;
    }
}
