package br.com.nogueiranogueira.aularefatoracao.service.reports;

import java.util.List;

public class RelatorioCSV extends GeradorRelatorioTemplate {
    @Override
    protected String formatarCabecalho() {
        return "Documento;Valor;Status\n";
    }

    @Override
    protected String formatarCorpo(List<String> dados) {
        StringBuilder sb = new StringBuilder();
        for (String dado : dados) {
            String[] partes = dado.split(" - ");
            if(partes.length == 3) {
                sb.append(partes[0]).append(";")
                  .append(partes[1]).append(";")
                  .append(partes[2]).append("\n");
            } else {
                sb.append(dado).append("\n");
            }
        }
        return sb.toString();
    }
}