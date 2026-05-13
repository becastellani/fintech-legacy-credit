package br.com.nogueiranogueira.aularefatoracao.blackbox;

import org.becastellani.validador.documento.Documento;
import org.becastellani.validador.service.ServicoValidadorDocumento;

/**
 * Módulo Black-box — Regra concreta para Crédito Digital.
 *
 * Implementa {@link RegraValidacaoFinanceira} com as regras específicas do produto
 * de crédito digital da plataforma:
 *  - Documento deve ser válido (validador-core)
 *  - Valor entre R$ 100,00 e R$ 50.000,00
 *
 * Acoplamento Black-box:
 *  Esta classe NÃO conhece o motor que a executa ({@link MotorValidacaoBlackBox}).
 *  Ela apenas cumpre o contrato da interface. O motor é a "caixa-preta": quem usa
 *  a regra não precisa saber como ela é executada internamente — e vice-versa.
 */
public class RegraValidacaoCreditoDigital implements RegraValidacaoFinanceira {

    private static final double VALOR_MINIMO  = 100.00;
    private static final double VALOR_MAXIMO  = 50_000.00;

    private final ServicoValidadorDocumento servicoValidador;

    public RegraValidacaoCreditoDigital() {
        this.servicoValidador = new ServicoValidadorDocumento();
    }

    @Override
    public boolean validar(Documento documento, double valor) {
        ServicoValidadorDocumento.ResultadoValidacao resultado = servicoValidador.validar(documento);
        if (!resultado.valido()) {
            System.out.println("[BLACKBOX][CREDITO_DIGITAL] " + resultado.mensagem());
            return false;
        }
        if (valor < VALOR_MINIMO || valor > VALOR_MAXIMO) {
            System.out.println("[BLACKBOX][CREDITO_DIGITAL] Valor fora do range permitido: R$"
                    + String.format("%.2f", valor));
            return false;
        }
        System.out.println("[BLACKBOX][CREDITO_DIGITAL] " + resultado.mensagem()
                + " | Valor R$" + String.format("%.2f", valor) + " aprovado.");
        return true;
    }

    @Override
    public String descricao() {
        return "Crédito Digital (R$" + VALOR_MINIMO + " – R$" + VALOR_MAXIMO + ")";
    }
}
