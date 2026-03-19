package br.com.nogueiranogueira.aularefatoracao.service.reports;

import java.util.List;

public class RelatorioPDF extends GeradorRelatorioTemplate {

    @Override
    protected String formatarCabecalho() {
        return "==== RELATÓRIO DE APROVAÇÕES ====\n" +
               "Documento | Valor | Status\n" +
               "---------------------------------\n";
    }

    @Override
    protected String formatarCorpo(List<String> dados) {
        StringBuilder sb = new StringBuilder();
        for (String dado : dados) {
            String[] partes = dado.split(" - ");
            if (partes.length == 3) {
                sb.append(String.format("%-14s | %-7s | %s\n", partes[0], partes[1], partes[2]));
            } else {
                sb.append(dado).append("\n");
            }
        }
        sb.append("=================================");
        return sb.toString();
    }
}