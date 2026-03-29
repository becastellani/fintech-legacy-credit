package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.factory.AnaliseCreditoFactory;
import br.com.nogueiranogueira.aularefatoracao.factory.ValidadorDocumentoFactory;
import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.model.TipoConta;
import br.com.nogueiranogueira.aularefatoracao.repository.SolicitacaoCreditoRepository;
import br.com.nogueiranogueira.aularefatoracao.strategy.AnaliseStrategy;

import br.com.nogueiranogueira.aularefatoracao.strategy.documento.ValidadorDocumentoStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AnaliseCreditoService {

    private static final int SCORE_MINIMO = 500;

    private final SolicitacaoCreditoRepository repository;
    private final ServicoAnaliseRisco servicoAnaliseRisco;

    public AnaliseCreditoService(
            SolicitacaoCreditoRepository repository,
            @Qualifier("serasa") ServicoAnaliseRisco servicoAnaliseRisco){
        this.repository = repository;
        this.servicoAnaliseRisco = servicoAnaliseRisco;
    }

    public boolean analisarSolicitacao(String documento, String cliente, double valor, int score,
                                       boolean negativado, String tipoConta) {
        log.info("Iniciando análise para: {}", cliente);
        ValidadorDocumentoStrategy validador = ValidadorDocumentoFactory.obter(documento);
        if (!validador.validar(documento)) {
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

        // Consulta ao bureau de crédito externo via Adapter (SOAP/Serasa)
        boolean aprovadoPeloBureau = consultarBureauCredito(documento, valor, score);
        if (!aprovadoPeloBureau) {
            log.warn("Solicitação reprovada pelo bureau externo para documento: {}", documento);
            persistirResultado(documento, cliente, valor, score, negativado, tipoConta, false,
                    "Reprovado pelo bureau de crédito externo");
            return false;
        }

        // Regras internas de negócio via Strategy
        AnaliseStrategy strategy = AnaliseCreditoFactory.obterEstrategia(tipo);
        boolean aprovado = strategy.analisar(valor, score);

        String motivo = aprovado ? null : "Reprovado pelas regras de " + tipoConta;
        persistirResultado(documento, cliente, valor, score, negativado, tipoConta, aprovado, motivo);
        return aprovado;
    }

    public void processarLote(List<SolicitacaoCredito> solicitacoes) {
        for (SolicitacaoCredito s : solicitacoes) {
            analisarSolicitacao(
                    s.getDocumento(),
                    s.getCliente(),
                    s.getValor(),
                    s.getScore(),
                    s.getNegativado(),
                    s.getTipoConta()
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

    /**
     * Consulta o bureau de crédito externo via Adapter (padrão Adapter).
     * Monta um DTO transiente com os dados mínimos necessários para a chamada.
     */
    private boolean consultarBureauCredito(String documento, double valor, int score) {
        log.info("Consultando bureau de crédito externo para documento: {}", documento);
        SolicitacaoCredito dto = new SolicitacaoCredito();
        dto.setDocumento(documento);
        dto.setValor(valor);
        dto.setScore(score);
        return servicoAnaliseRisco.avaliarCredito(dto);
    }

    private void persistirResultado(String documento, String cliente, double valor, int score, boolean negativado,
                                    String tipoConta, boolean aprovado, String motivo) {
        try {
            SolicitacaoCredito solicitacao = new SolicitacaoCredito();
            solicitacao.setDocumento(documento);
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
}
