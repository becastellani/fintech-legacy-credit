package br.com.nogueiranogueira.aularefatoracao.service.reports;

import java.util.List;

public class RelatorioPDF extends GeradorRelatorioTemplate {
    @Override
    protected String formatarCabecalho() {
        return "--- RELATÓRIO DE AUDITORIA (PDF) ---\n";
    }

    @Override
    protected String formatarCorpo(List<String> dados) {
        StringBuilder sb = new StringBuilder();
        dados.forEach(d -> sb.append("[PAGINA] ").append(d).append("\n"));
        return sb.toString();
    }
}