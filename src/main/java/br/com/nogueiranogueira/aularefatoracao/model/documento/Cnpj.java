package br.com.nogueiranogueira.aularefatoracao.model.documento;

/**
 * Documento de Pessoa Jurídica brasileira (CNPJ — 14 dígitos).
 * Parte da hierarquia sealed: cada país tem seus tipos de documento concretos.
 */
public record Cnpj(String valor) implements Documento {
}
