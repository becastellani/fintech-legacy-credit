package br.com.nogueiranogueira.aularefatoracao.service;

import br.com.nogueiranogueira.aularefatoracao.factory.AnalisePaisStrategyFactory;
import org.becastellani.validador.factory.ValidadorDocumentoFactory;
import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.strategy.analisePais.AnalisePaisStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LPS — Core Asset: Processador Central de Crédito.
 *
 * Responsabilidade única: orquestrar as regras de análise de crédito
 * sem nenhum if/else de tipo ou país — toda a variabilidade é resolvida
 * pelas fábricas de estratégia.
 *
 * Fluxo:
 *   1. Valida o documento via {@link ValidadorDocumentoFactory} (variante pelo formato + país)
 *   2. Aplica as regras do país via {@link AnalisePaisStrategyFactory} (variante pelo país)
 *
 * Esta classe não sabe quantos países ou formatos de documento existem.
 * Adicionar Portugal, Argentina ou qualquer outro mercado = criar as variantes
 * e registrar nas fábricas. Zero alteração aqui.
 */
@Slf4j
@Component
public class ProcessadorCreditoCore {

    private static final int SCORE_MINIMO_GLOBAL = 500;

    /**
     * Processa a solicitação de crédito aplicando as regras do país informado.
     *
     * @param solicitacao dados da solicitação (documento, score, valor, negativado)
     * @param pais        código ISO do país (BR, US, MX, PT…)
     * @return {@code true} se aprovado, {@code false} caso contrário
     */
    public boolean processar(SolicitacaoCredito solicitacao, String pais) {
        String documento = solicitacao.getDocumento();

        // Ponto de variação 1: validação do documento (variante por formato + país)
        boolean documentoValido = ValidadorDocumentoFactory.obter(documento, pais)
                .map(v -> v.validar(documento))
                .orElse(false);

        if (!documentoValido) {
            log.warn("[Core] Documento inválido para país {}: {}", pais, documento);
            return false;
        }

        // Regra de negócio comum (core): score mínimo global
        if (solicitacao.getScore() <= SCORE_MINIMO_GLOBAL) {
            log.warn("[Core] Score insuficiente: {}", solicitacao.getScore());
            return false;
        }

        // Ponto de variação 2: regras de aprovação por país (variante por mercado)
        try {
            AnalisePaisStrategy estrategia = AnalisePaisStrategyFactory.criar(pais);
            boolean aprovado = estrategia.aprovar(solicitacao);
            log.info("[Core] Resultado para país {} / score {}: {}", pais, solicitacao.getScore(), aprovado);
            return aprovado;
        } catch (IllegalArgumentException e) {
            log.warn("[Core] País não suportado: {}", pais);
            return false;
        }
    }
}
