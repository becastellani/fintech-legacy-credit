package br.com.nogueiranogueira.aularefatoracao.strategy.documento;

/**
 * LPS Variante — Validação de CURP (Clave Única de Registro de Población) do México.
 *
 * Formato: 18 caracteres alfanuméricos.
 * Estrutura: AAAA000000HSSSSSD#
 *   - 4 letras iniciais (sobrenome + nome + sexo)
 *   - 6 dígitos de data de nascimento (AAMMDD)
 *   - 1 letra para sexo (H/M)
 *   - 2 letras para estado
 *   - 3 consonantes internas do nome
 *   - 1 dígito homoclave
 *   - 1 dígito verificador
 *
 * Para fins didáticos: valida o formato via regex, sem cálculo do dígito verificador.
 */
public final class CurpValidadorStrategy implements ValidadorDocumentoStrategy {

    // Regex oficial da CURP mexicana
    private static final String CURP_REGEX =
            "^[A-Z]{4}\\d{6}[HM][A-Z]{2}[B-DF-HJ-NP-TV-Z]{3}[A-Z\\d]\\d$";

    @Override
    public boolean validar(String documento) {
        if (documento == null || documento.isBlank()) return false;
        String limpo = documento.trim().toUpperCase();
        if (limpo.length() != 18) return false;
        return limpo.matches(CURP_REGEX);
    }
}
