package br.com.nogueiranogueira.aularefatoracao.service.reports;

import java.util.List;

public abstract class GeradorRelatorioTemplate {

    public final void gerarRelatorio(String dataReferencia) {
        System.out.println("--- Iniciando Geração de Relatório para: " + dataReferencia + " ---");
        List<String> dados = extrairDadosDoBanco();
        
        if (dados.isEmpty()) {
            System.out.println("Sem dados para exportar.");
            return;
        }

        String cabecalho = formatarCabecalho();
        String corpo = formatarCorpo(dados);
        
        salvarArquivo(cabecalho + corpo);
    }

    private List<String> extrairDadosDoBanco() {
        return List.of("123.456.789-00 - R$ 5000 - APROVADO", "987.654.321-11 - R$ 1000 - APROVADO");
    }

    private void salvarArquivo(String conteudo) {
        System.out.println("[Disco] Conteúdo salvo com sucesso:\n" + conteudo);
    }

    protected abstract String formatarCabecalho();
    protected abstract String formatarCorpo(List<String> dados);
}