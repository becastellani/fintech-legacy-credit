package br.com.becastellani.aularefatoracao.service;

import br.com.becastellani.aularefatoracao.model.TipoConta;
import br.com.becastellani.aularefatoracao.strategy.AnaliseStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessadorCreditoService {

    private final List<AnaliseStrategy> strategies;

    /**
     * Processa uma lista de clientes em paralelo usando Virtual Threads (Java 21).
     * Cada solicitação individual é resolvida pela strategy elegível para o tipo de conta.
     */
    public void processarLote(List<String> clientes) {
        log.info("Iniciando processamento em lote para {} clientes", clientes.size());

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String cliente : clientes) {
                executor.submit(() -> processarIndividual(cliente, 1000.0, 600, "PF"));
            }
        }

        log.info("Processamento em lote concluído");
    }

    /**
     * Processa uma única solicitação localizando a strategy elegível para o tipoConta.
     */
    public boolean processarIndividual(String cliente, double valor, int score, String tipoConta) {
        log.info("Processando solicitação individual — cliente: {}, tipoConta: {}", cliente, tipoConta);

        TipoConta tipo;
        try {
            tipo = TipoConta.fromString(tipoConta);
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de conta inválido: {}", tipoConta);
            return false;
        }

        return strategies.stream()
                .filter(strategy -> strategy.elegivel(tipo))
                .findFirst()
                .map(strategy -> strategy.analisar(valor, score))
                .orElseGet(() -> {
                    log.warn("Nenhuma strategy elegível encontrada para tipo: {}", tipoConta);
                    return false;
                });
    }
}

