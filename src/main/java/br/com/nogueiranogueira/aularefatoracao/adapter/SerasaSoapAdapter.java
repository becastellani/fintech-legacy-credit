package br.com.nogueiranogueira.aularefatoracao.adapter;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.service.ServicoAnaliseRisco;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

/**
 * Padrão Adapter — traduz a interface interna {@link ServicoAnaliseRisco}
 * para o protocolo SOAP/XML exigido pela API legada da Serasa.
 *
 * O chamador (AnaliseCreditoService) só conhece a interface Target.
 * Toda a complexidade do SOAP fica encapsulada aqui.
 */
@Slf4j
@Component("serasa")
public class SerasaSoapAdapter extends AnaliseRiscoAdapterTemplate {

    private static final String SERASA_QA_ENDPOINT = "https://qa.serasa.com.br/ws/ConsultaCredito";

    @Override
    protected String construirPayload(SolicitacaoCredito solicitacao) {
        log.info("[Adapter] Iniciando tradução do domínio para SOAP/XML...");
        String soapPayload = """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ser="http://serasa.com.br/ws">
                <soapenv:Header/>
                <soapenv:Body>
                    <ser:ConsultarRisco>
                        <documento>%s</documento>
                        <valorSolicitado>%s</valorSolicitado>
                        <scoreInterno>%d</scoreInterno>
                    </ser:ConsultarRisco>
                </soapenv:Body>
            </soapenv:Envelope>
            """.formatted(
            solicitacao.getDocumento(),
            solicitacao.getValor().toString(),
            solicitacao.getScore()
            );
        return soapPayload;
    }

    @Override
    protected String enviarRequisicao(String payload) throws Exception {
        // 2. CHAMADA DE REDE via HttpClient moderno (Java 11+)
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SERASA_QA_ENDPOINT))
                .header("Content-Type", "text/xml;charset=UTF-8")
                .header("SOAPAction", "ConsultarRisco")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        log.info("[Adapter] Enviando requisição para a API legada: {}", SERASA_QA_ENDPOINT);

        // Em produção: client.send(request, HttpResponse.BodyHandlers.ofString()).body()
        // Aqui simulamos a resposta para fins didáticos:
        String xmlResposta = simularRespostaDaApiExterna();

        // 3. TRADUÇÃO DE VOLTA: XML → booleano do domínio
        log.info("[Adapter] Traduzindo resposta XML para domínio...");
        return xmlResposta;

    }

    @Override
    protected boolean interpretarResposta(String resposta) {
        /**
         * Extração simples para fins didáticos.
         * Em produção usaríamos XPath ou JAXB para parsing robusto.
         */
        return resposta != null && resposta.contains("<statusConsulta>APROVADO");
    }

    private String simularRespostaDaApiExterna() {
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
                    <soapenv:Body>
                        <ConsultarRiscoResponse>
                            <statusConsulta>APROVADO_BAIXO_RISCO</statusConsulta>
                            <codigoRetorno>00</codigoRetorno>
                            <limiteSugerido>5000.00</limiteSugerido>
                        </ConsultarRiscoResponse>
                    </soapenv:Body>
                </soapenv:Envelope>
                """;
    }
}
