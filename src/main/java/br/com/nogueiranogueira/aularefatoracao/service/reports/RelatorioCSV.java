package br.com.nogueiranogueira.aularefatoracao.service.reports;

import java.util.List;

public class RelatorioCSV extends GeradorRelatorioTemplate {
    @Override
    protected String formatarCabecalho() {
        return "Documento;Status;Valor\n";
    }

    @Override
    protected String formatarCorpo(List<String> dados) {
        return String.join("\n", dados);
    }
}