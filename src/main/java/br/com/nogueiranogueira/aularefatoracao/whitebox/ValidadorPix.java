package br.com.nogueiranogueira.aularefatoracao.whitebox;

import org.becastellani.validador.documento.Documento;
import org.becastellani.validador.service.ServicoValidadorDocumento;

/**
 * Módulo White-box — Implementação concreta para validação de transações Pix.
 *
 * Estende {@link ValidadorFinanceiroWhiteBox} e implementa o hook {@code executar()},
 * plugando as regras específicas do produto Pix dentro do fluxo orquestrado pelo framework.
 *
 * Regras de negócio do Pix (simuladas):
 *  - Documento deve ser válido (delegado ao ServicoValidadorDocumento do validador-core)
 *  - Limite por transação: R$ 20.000,00 (regra BACEN para Pix noturno, simplificada)
 *  - Valor deve ser positivo
 *
 * Acoplamento White-box:
 *  Esta classe CONHECE a hierarquia de {@link ValidadorFinanceiroWhiteBox} — é necessário
 *  entender a estrutura interna do framework para estendê-lo corretamente. Isso é o que
 *  caracteriza a "caixa-branca": visibilidade e dependência da estrutura do pai.
 */
public class ValidadorPix extends ValidadorFinanceiroWhiteBox {

    private static final double LIMITE_PIX = 20_000.00;

    private final ServicoValidadorDocumento servicoValidador;

    public ValidadorPix() {
        this.servicoValidador = new ServicoValidadorDocumento();
    }

    /**
     * Hook — implementa as regras do produto Pix.
     *
     * Este método é chamado pelo framework (Template Method em {@link ValidadorFinanceiroWhiteBox#validar}).
     * A subclasse não controla quando é chamada — apenas define o comportamento específico.
     */
    @Override
    protected boolean executar(Documento documento, double valor) {
        if (valor <= 0 || valor > LIMITE_PIX) {
            System.out.println("[WHITEBOX][PIX] Valor fora do limite: R$" + String.format("%.2f", valor));
            return false;
        }

        ServicoValidadorDocumento.ResultadoValidacao resultado = servicoValidador.validar(documento);
        if (!resultado.valido()) {
            System.out.println("[WHITEBOX][PIX] " + resultado.mensagem());
            return false;
        }

        System.out.println("[WHITEBOX][PIX] " + resultado.mensagem() + " | Pix liberado.");
        return true;
    }
}
