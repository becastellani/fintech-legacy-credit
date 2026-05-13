package br.com.nogueiranogueira.aularefatoracao.blackbox;

import org.becastellani.validador.documento.Documento;

/**
 * Módulo Black-box — Contrato de regra de validação financeira.
 *
 * Interface que define o ponto de variação "Como validar uma operação financeira?".
 * Qualquer nova regra de produto (Crédito, TED, Câmbio, etc.) implementa este contrato
 * sem alterar o motor de execução ({@link MotorValidacaoBlackBox}).
 *
 * Isso é o Open/Closed Principle em ação: o motor está fechado para modificação,
 * mas aberto para extensão via novas implementações desta interface.
 */
public interface RegraValidacaoFinanceira {

    /**
     * Executa a regra de validação para o documento e valor informados.
     *
     * @param documento documento de identidade do cliente
     * @param valor     valor da operação financeira
     * @return true se a operação é aprovada pelas regras do produto
     */
    boolean validar(Documento documento, double valor);

    /**
     * Descrição legível da regra — usada em logs e auditoria.
     */
    String descricao();
}
