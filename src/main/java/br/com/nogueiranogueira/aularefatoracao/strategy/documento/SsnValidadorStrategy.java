package br.com.nogueiranogueira.aularefatoracao.strategy.documento;

/**
 * LPS Variante — Validação de SSN (Social Security Number) dos EUA.
 *
 * Formato: XXX-XX-XXXX (9 dígitos).
 * Regras básicas:
 *   - Área (3 primeiros dígitos) não pode ser 000, 666 ou ≥ 900
 *   - Grupo (2 dígitos centrais) não pode ser 00
 *   - Serial (4 últimos dígitos) não pode ser 0000
 */
public final class SsnValidadorStrategy implements ValidadorDocumentoStrategy {

    @Override
    public boolean validar(String documento) {
        if (documento == null || documento.isBlank()) return false;
        String limpo = documento.replaceAll("[^0-9]", "");
        if (limpo.length() != 9) return false;
        return isSsnValido(limpo);
    }

    private boolean isSsnValido(String ssn) {
        // Rejeita sequências triviais
        if (ssn.matches("(\\d)\\1{8}")) return false;

        String area   = ssn.substring(0, 3);
        String grupo  = ssn.substring(3, 5);
        String serial = ssn.substring(5, 9);

        if (area.equals("000") || area.equals("666")) return false;
        if (Integer.parseInt(area) >= 900)            return false;
        if (grupo.equals("00"))                       return false;
        if (serial.equals("0000"))                    return false;

        return true;
    }
}
