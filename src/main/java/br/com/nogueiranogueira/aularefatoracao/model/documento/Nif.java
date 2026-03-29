package br.com.nogueiranogueira.aularefatoracao.model.documento;

/**
 * NIF — Número de Identificação Fiscal (Portugal).
 * Variante do ponto de variação "Documento" para o mercado português.
 */
public record Nif(String valor) implements Documento {
}
