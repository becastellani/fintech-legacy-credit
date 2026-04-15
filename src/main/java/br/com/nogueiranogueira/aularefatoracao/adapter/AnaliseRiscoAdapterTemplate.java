package br.com.nogueiranogueira.aularefatoracao.adapter;

import org.becastellani.validador.factory.ValidadorDocumentoFactory;
import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.service.ServicoAnaliseRisco;

public abstract class AnaliseRiscoAdapterTemplate implements ServicoAnaliseRisco {
    public final boolean avaliarCredito(SolicitacaoCredito solicitacaoCredito) {
        /**
         * Valida o documento antes de qualquer chamada externa
         */
        boolean documentoValido = ValidadorDocumentoFactory.obter(solicitacaoCredito.getDocumento())
                .map(v -> v.validar(solicitacaoCredito.getDocumento()))
                .orElse(false);

        if (!documentoValido) {
            return false;
        }
        try {
            /**
             * Construir payload (XML para SOAP, JSON para REST)
             * Enviar requisição (headers SOAP vs headers REST)
             * Interpretar resposta (parse XML vs parse JSON)
             */
            String payload = construirPayload(solicitacaoCredito);
            String resposta = enviarRequisicao(payload);
            return interpretarResposta(resposta);
        } catch (Exception e) {
            return false;
        }
    }
    protected abstract String construirPayload(SolicitacaoCredito solicitacaoCredito);
    protected abstract String enviarRequisicao(String payload) throws Exception;
    protected abstract boolean interpretarResposta(String resposta);
}
