package br.com.nogueiranogueira.aularefatoracao.strategy.documento;

/**
 * LPS — Ponto de Variação: "Como validar o documento do cliente?"
 *
 * Interface sealed: o compilador garante que apenas as variantes conhecidas
 * (CPF, CNPJ, SSN, CURP) podem implementar este contrato, fechando o ecossistema
 * de validadores suportados pela plataforma.
 */
public sealed interface ValidadorDocumentoStrategy
        permits CpfValidadorStrategy, CnpjValidadorStrategy, SsnValidadorStrategy, CurpValidadorStrategy, NifValidadorStrategy {

    boolean validar(String documento);
}
