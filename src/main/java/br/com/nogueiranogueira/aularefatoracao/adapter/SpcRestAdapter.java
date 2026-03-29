package br.com.nogueiranogueira.aularefatoracao.adapter;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

@Slf4j
@Component("spc")
public class SpcRestAdapter extends AnaliseRiscoAdapterTemplate {

    private static final String SPC_ENDPOINT = "https://qa.spc.com.br/api/v1/credito";

    @Override
    protected String construirPayload(SolicitacaoCredito solicitacao) {
        log.info("[Adapter] Iniciando tradução do domínio para REST...");
        return """
            {
                "documento": "%s",
                "valor": %s,
                "score": %d
            }
            """.formatted(
                solicitacao.getDocumento(),
                solicitacao.getValor().toString(),
                solicitacao.getScore()
        );
    }
    @Override
    protected String enviarRequisicao(String payload) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SPC_ENDPOINT))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        log.info("[Adapter] Enviando requisição REST para: {}", SPC_ENDPOINT);

        return simularRespostaDaApiExterna();
    }

    @Override
    protected boolean interpretarResposta(String resposta) {
        // A resposta simulada usa a chave "statusConsulta" com valor "APROVADO_BAIXO_RISCO"
        return resposta != null && resposta.contains("\"statusConsulta\": \"APROVADO");
    }
    private String simularRespostaDaApiExterna() {
        return """
            {
                "statusConsulta": "APROVADO_BAIXO_RISCO",
                "limiteSugerido": 5000.00,
                "codigoRetorno": "00"
            }
            """;
    }
}
