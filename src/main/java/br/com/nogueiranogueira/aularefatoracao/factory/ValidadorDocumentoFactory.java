package br.com.nogueiranogueira.aularefatoracao.factory;

import br.com.nogueiranogueira.aularefatoracao.strategy.documento.CnpjValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.CpfValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.CurpValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.NifValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.SsnValidadorStrategy;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.ValidadorDocumentoStrategy;

import java.util.Optional;

/**
 * LPS — Fábrica de Variantes para o ponto de variação "ValidadorDocumentoStrategy".
 *
 * Lógica de seleção (com contexto de país):
 *   - 11 dígitos numéricos              → CPF  (BR, pessoa física)
 *   - 14 dígitos numéricos              → CNPJ (BR, pessoa jurídica)
 *   -  9 dígitos numéricos + país "PT"  → NIF  (Portugal)
 *   -  9 dígitos numéricos + outros     → SSN  (EUA / padrão)
 *   - 18 alfanuméricos                  → CURP (México)
 *
 * Problema de design:
 *   NIF (Portugal) e SSN (EUA) têm o mesmo formato superficial: 9 dígitos numéricos.
 *   A desambiguação exige o contexto do país. Por isso existe o overload {@code obter(doc, pais)}.
 *   O overload sem país assume SSN para documentos de 9 dígitos (comportamento legado).
 *
 * Retorna Optional.empty() para formatos não reconhecidos — sem lançar exceção.
 */
public class ValidadorDocumentoFactory {
    private ValidadorDocumentoFactory() { }

    /**
     * Overload sem país: mantém compatibilidade. Para 9 dígitos assume SSN (EUA).
     */
    public static Optional<ValidadorDocumentoStrategy> obter(String documento) {
        return obter(documento, "US");
    }

    /**
     * Resolve o validador com contexto de país para desambiguar formatos idênticos (NIF vs SSN).
     *
     * @param documento string do documento (com ou sem pontuação)
     * @param pais      código ISO do país (BR, US, MX, PT…)
     * @return Optional com a estratégia correta, ou empty() se o formato não for reconhecido.
     */
    public static Optional<ValidadorDocumentoStrategy> obter(String documento, String pais) {
        if (documento == null || documento.isBlank()) return Optional.empty();

        String apenasNumeros      = documento.replaceAll("[^0-9]", "");
        String apenasAlfanumerico = documento.replaceAll("[^A-Za-z0-9]", "");
        String paisUpper          = (pais == null) ? "BR" : pais.toUpperCase();

        return switch (apenasNumeros.length()) {
            case 9  -> "PT".equals(paisUpper)
                    ? Optional.of(new NifValidadorStrategy())
                    : Optional.of(new SsnValidadorStrategy());
            case 11 -> Optional.of(new CpfValidadorStrategy());
            case 14 -> Optional.of(new CnpjValidadorStrategy());
            default -> apenasAlfanumerico.length() == 18
                    ? Optional.of(new CurpValidadorStrategy())
                    : Optional.empty();
        };
    }
}
