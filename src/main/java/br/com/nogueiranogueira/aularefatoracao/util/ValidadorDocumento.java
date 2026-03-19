package br.com.nogueiranogueira.aularefatoracao.util;

import org.springframework.stereotype.Component;

/**
 * Valida documentos CPF e CNPJ usando o algoritmo oficial dos dígitos verificadores.
 *
 * A validação consiste em três etapas:
 *   1. Limpeza — remove pontuação, mantém apenas dígitos.
 *   2. Sanidade — comprimento correto e sem sequências repetidas (ex: "000.000.000-00").
 *   3. Dígitos verificadores — cálculo módulo 11 conforme a Receita Federal.
 */
@Component
public class ValidadorDocumento {

    public boolean isDocumentoValido(String documento) {
        if (documento == null || documento.isBlank()) {
            return false;
        }

        String limpo = documento.replaceAll("[^0-9]", "");

        return switch (limpo.length()) {
            case 11 -> isCpfValido(limpo);
            case 14 -> isCnpjValido(limpo);
            default -> false;
        };
    }

    // ──────────────────────────────────────────────────────────────────
    // CPF
    // ──────────────────────────────────────────────────────────────────

    private boolean isCpfValido(String cpf) {
        // Rejeita sequências triviais como "111.111.111-11"
        if (cpf.matches("(\\d)\\1{10}")) return false;

        int primeiroDigito = calcularDigitoCpf(cpf, 9);
        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) return false;

        int segundoDigito = calcularDigitoCpf(cpf, 10);
        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    /**
     * Calcula um dígito verificador de CPF.
     *
     * @param cpf    string de 11 dígitos
     * @param length quantos dígitos usar no cálculo (9 para o 1º, 10 para o 2º)
     */
    private int calcularDigitoCpf(String cpf, int length) {
        int soma = 0;
        for (int i = 0; i < length; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (length + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return (resto == 10 || resto == 11) ? 0 : resto;
    }

    // ──────────────────────────────────────────────────────────────────
    // CNPJ
    // ──────────────────────────────────────────────────────────────────

    private boolean isCnpjValido(String cnpj) {
        // Rejeita sequências triviais como "00.000.000/0000-00"
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        int primeiroDigito = calcularDigitoCnpj(cnpj, 12);
        if (primeiroDigito != Character.getNumericValue(cnpj.charAt(12))) return false;

        int segundoDigito = calcularDigitoCnpj(cnpj, 13);
        return segundoDigito == Character.getNumericValue(cnpj.charAt(13));
    }

    /**
     * Calcula um dígito verificador de CNPJ.
     *
     * Os pesos ciclam de 2 a 9 da direita para a esquerda:
     *   12 primeiros dígitos → pesos [5,4,3,2,9,8,7,6,5,4,3,2]
     *   13 primeiros dígitos → pesos [6,5,4,3,2,9,8,7,6,5,4,3,2]
     *
     * @param cnpj   string de 14 dígitos
     * @param length quantos dígitos usar no cálculo (12 ou 13)
     */
    private int calcularDigitoCnpj(String cnpj, int length) {
        int soma = 0;
        int peso = length - 7; // peso inicial: 5 para 12 dígitos, 6 para 13 dígitos
        for (int i = 0; i < length; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * peso;
            peso = (peso == 2) ? 9 : peso - 1;
        }
        int resto = soma % 11;
        return (resto < 2) ? 0 : 11 - resto;
    }
}
