package br.com.nogueiranogueira.aularefatoracao.model.documento;

/**
 * Tipo base sealed para documentos de identidade.
 *
 * LPS — Ponto de Variação: "Qual documento representa o cliente?"
 * Variantes conhecidas (permits): CPF (BR pessoa física), CNPJ (BR pessoa jurídica),
 *                                  SSN (EUA), CURP (México).
 *
 * O compilador garante que nenhuma implementação fora deste módulo pode existir,
 * fechando o ecossistema de documentos suportados pela plataforma.
 */
public sealed interface Documento permits Cpf, Cnpj, Ssn, Curp, Nif {
}