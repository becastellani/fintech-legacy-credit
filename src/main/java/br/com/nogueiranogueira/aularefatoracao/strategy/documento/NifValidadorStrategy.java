package br.com.nogueiranogueira.aularefatoracao.strategy.documento;

/**
 * LPS — Variante: validação de NIF (Número de Identificação Fiscal, Portugal).
 *
 * Regras:
 *   - 9 dígitos numéricos
 *   - Primeiro dígito: 1, 2, 3, 5, 6, 7, 8 ou 9 (0 e 4 são inválidos)
 *   - Dígito verificador (posição 9): módulo 11
 *       soma = Σ(dígito[i] × (10 - i)) para i = 1..8
 *       resto = soma % 11
 *       dígito_verificador = (resto == 0 || resto == 1) ? 0 : 11 - resto
 */
public final class NifValidadorStrategy implements ValidadorDocumentoStrategy {

    private static final String PRIMEIROS_DIGITOS_VALIDOS = "123456789";

    @Override
    public boolean validar(String documento) {
        if (documento == null || documento.isBlank()) return false;
        String limpo = documento.replaceAll("[^0-9]", "");
        if (limpo.length() != 9) return false;
        return isNifValido(limpo);
    }

    private boolean isNifValido(String nif) {
        char primeiroDigito = nif.charAt(0);
        if (PRIMEIROS_DIGITOS_VALIDOS.indexOf(primeiroDigito) < 0) return false;

        int soma = 0;
        for (int i = 0; i < 8; i++) {
            soma += Character.getNumericValue(nif.charAt(i)) * (10 - i);
        }
        int resto = soma % 11;
        int digitoEsperado = (resto == 0 || resto == 1) ? 0 : 11 - resto;
        return Character.getNumericValue(nif.charAt(8)) == digitoEsperado;
    }
}
