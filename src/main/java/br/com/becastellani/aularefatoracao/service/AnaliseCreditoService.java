package br.com.becastellani.aularefatoracao.service;

import br.com.becastellani.aularefatoracao.model.SolicitacaoCredito;
import br.com.becastellani.aularefatoracao.model.TipoConta;
import br.com.becastellani.aularefatoracao.repository.SolicitacaoCreditoRepository;
import br.com.becastellani.aularefatoracao.strategy.AnaliseStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnaliseCreditoService {

    private static final int SCORE_MINIMO = 500;

    private final List<AnaliseStrategy> strategies;
    private final SolicitacaoCreditoRepository repository;

    public boolean analisarSolicitacao(String cliente, double valor, int score,
                                       boolean negativado, String tipoConta) {
        log.info("Iniciando análise para: {}", cliente);

        if (valor <= 0) {
            log.warn("Valor inválido: {}", valor);
            persistirResultado(cliente, valor, score, negativado, tipoConta, false, "Valor inválido");
            return false;
        }
        if (negativado) {
            log.warn("Cliente negativado: {}", cliente);
            persistirResultado(cliente, valor, score, negativado, tipoConta, false, "Cliente negativado");
            return false;
        }
        if (score <= SCORE_MINIMO) {
            log.warn("Score baixo: {}", score);
            persistirResultado(cliente, valor, score, negativado, tipoConta, false, "Score abaixo do mínimo");
            return false;
        }

        TipoConta tipo;
        try {
            tipo = TipoConta.fromString(tipoConta);
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de conta desconhecido: {}", tipoConta);
            persistirResultado(cliente, valor, score, negativado, tipoConta, false, "Tipo de conta inválido");
            return false;
        }

        consultarBureauCredito();

        boolean aprovado = strategies.stream()
                .filter(strategy -> strategy.elegivel(tipo))
                .findFirst()
                .map(strategy -> strategy.analisar(valor, score))
                .orElseGet(() -> {
                    log.warn("Nenhuma strategy encontrada para tipo: {}", tipoConta);
                    return false;
                });

        String motivo = aprovado ? null : "Reprovado pelas regras de " + tipoConta;
        persistirResultado(cliente, valor, score, negativado, tipoConta, aprovado, motivo);
        return aprovado;
    }

    public void processarLote(List<String> clientes) {
        for (String cliente : clientes) {
            analisarSolicitacao(cliente, 1000.0, 600, false, "PF");
        }
    }

    public List<SolicitacaoCredito> obterSolicitacoesPorCliente(String cliente) {
        return repository.findByCliente(cliente);
    }

    public List<SolicitacaoCredito> obterSolicitacoesAprovadas() {
        return repository.findByAprovado(true);
    }

    // ──────────────────────────────────────────────────────────────────
    // Métodos privados
    // ──────────────────────────────────────────────────────────────────

    private void persistirResultado(String cliente, double valor, int score, boolean negativado,
                                    String tipoConta, boolean aprovado, String motivo) {
        try {
            SolicitacaoCredito solicitacao = new SolicitacaoCredito();
            solicitacao.setCliente(cliente);
            solicitacao.setValor(valor);
            solicitacao.setScore(score);
            solicitacao.setNegativado(negativado);
            solicitacao.setTipoConta(tipoConta.toUpperCase());
            solicitacao.setAprovado(aprovado);
            solicitacao.setMotivoReprovacao(motivo);
            repository.save(solicitacao);
        } catch (Exception e) {
            log.error("Erro ao persistir resultado da análise para cliente: {}", cliente, e);
        }
    }

    private void consultarBureauCredito() {
        try {
            log.info("Consultando Bureau de Crédito Externo...");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Consulta ao bureau de crédito interrompida", e);
        }
    }
}