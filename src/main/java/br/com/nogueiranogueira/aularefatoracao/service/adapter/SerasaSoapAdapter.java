package br.com.nogueiranogueira.aularefatoracao.service.adapter;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.service.ServicoAnaliseRisco;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

@Component
public class SerasaSoapAdapter implements ServicoAnaliseRisco {

    private static final String SERASA_QA_ENDPOINT = "https://qa.serasa.com.br/ws/ConsultaCredito";

    @Override
    public boolean avaliarCredito(SolicitacaoCredito solicitacao) {
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
            """.formatted(solicitacao.getDocumento(), solicitacao.getValor(), solicitacao.getScore());

        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERASA_QA_ENDPOINT))
                    .header("Content-Type", "text/xml;charset=UTF-8")
                    .header("SOAPAction", "ConsultarRisco")
                    .POST(HttpRequest.BodyPublishers.ofString(soapPayload))
                    .build();

            String xmlResposta = simularRespostaDaApiExterna();
            return analisarXmlResposta(xmlResposta);

        } catch (Exception e) {
            return false;
        }
    }

    private String simularRespostaDaApiExterna() {
        return "<statusConsulta>APROVADO_BAIXO_RISCO</statusConsulta>";
    }

    private boolean analisarXmlResposta(String xml) {
        return xml != null && xml.contains("<statusConsulta>APROVADO");
    }
}