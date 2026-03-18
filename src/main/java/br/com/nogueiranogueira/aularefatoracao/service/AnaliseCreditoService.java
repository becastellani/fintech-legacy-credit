package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.factory.AnaliseCreditoFactory;
import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.model.TipoConta;
import br.com.nogueiranogueira.aularefatoracao.repository.SolicitacaoCreditoRepository;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategy;
import br.com.nogueiranogueira.aularefatoracao.util.ValidadorDocumento; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnaliseCreditoService {

    private static final int SCORE_MINIMO = 500;

    private final SolicitacaoCreditoRepository repository;
    
    private final ValidadorDocumento validador;

    public boolean analisarSolicitacao(String documento, String cliente, double valor, int score,
                                       boolean negativado, String tipoConta) {
        log.info("Iniciando análise para: {}", cliente);

        if (!validador.isDocumentoValido(documento)) {
            log.warn("Documento inválido: {}", documento);
            persistirResultado(documento, cliente, valor, score, negativado, tipoConta, false, "Documento inválido");
            return false;
        }

        if (valor <= 0) {
            log.warn("Valor inválido: {}", valor);
            persistirResultado(documento, cliente, valor, score, negativado, tipoConta, false, "Valor inválido");
            return false;
        }
        if (negativado) {
            log.warn("Cliente negativado: {}", cliente);
            persistirResultado(documento, cliente, valor, score, negativado, tipoConta, false, "Cliente negativado");
            return false;
        }
        if (score <= SCORE_MINIMO) {
            log.warn("Score baixo: {}", score);
            persistirResultado(documento, cliente, valor, score, negativado, tipoConta, false, "Score abaixo do mínimo");
            return false;
        }

        TipoConta tipo;
        try {
            tipo = TipoConta.fromString(tipoConta);
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de conta desconhecido: {}", tipoConta);
            persistirResultado(documento, cliente, valor, score, negativado, tipoConta, false, "Tipo de conta inválido");
            return false;
        }

        consultarBureauCredito();

        AnaliseStrategy strategy = AnaliseCreditoFactory.obterEstrategia(tipo);
        boolean aprovado = strategy.analisar(valor, score);

        String motivo = aprovado ? null : "Reprovado pelas regras de " + tipoConta;
        persistirResultado(documento, cliente, valor, score, negativado, tipoConta, aprovado, motivo);
        return aprovado;
    }

    public void processarLote(List<SolicitacaoCredito> solicitacoes) {
        for (SolicitacaoCredito solicitacao : solicitacoes) {
            analisarSolicitacao(
                    solicitacao.getDocumento(),
                    solicitacao.getCliente(),
                    solicitacao.getValor(),
                    solicitacao.getScore(),
                    solicitacao.getNegativado(),
                    solicitacao.getTipoConta()
            );
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

    private void persistirResultado(String documento, String cliente, double valor, int score, boolean negativado,
                                    String tipoConta, boolean aprovado, String motivo) {
        try {
            SolicitacaoCredito solicitacao = new SolicitacaoCredito();
            solicitacao.setDocumento(documento); // 5. Salvando o documento na entidade
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