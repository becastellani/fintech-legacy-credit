package br.com.nogueiranogueira.aularefatoracao.controller;

import br.com.nogueiranogueira.aularefatoracao.dto.SolicitacaoCreditoRecord;
import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.service.AnaliseCreditoService;
import br.com.nogueiranogueira.aularefatoracao.service.ProcessadorAnaliseCreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Solictações Controller", description = "Endpoints para análise e gerenciamento de solicitações de crédito")
@RestController
@RequestMapping("/solicitacoes")
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoCreditoController {

    @Autowired
    private final AnaliseCreditoService analiseCreditoService;

    @Autowired
    private final ProcessadorAnaliseCreditoService processadorAnaliseCreditoService;


    @Operation(summary = "Analisar solicitação de crédito", description = "Analisa uma solicitação de crédito com base nos parâmetros fornecidos e retorna o resultado da análise.")
    @PostMapping("/analisar")
    public ResponseEntity<Map<String, Object>> analisarSolicitacao(
            @RequestParam String cliente,
            @RequestParam Double valor,
            @RequestParam Integer score,
            @RequestParam(defaultValue = "false") Boolean negativado,
            @RequestParam(defaultValue = "PF") String tipoConta) {

        log.info("Recebida requisição de análise para cliente: {}", cliente);

        try {
            boolean aprovado = analiseCreditoService.analisarSolicitacao(cliente, valor, score, negativado, tipoConta);

            Map<String, Object> response = new HashMap<>();
            response.put("cliente", cliente);
            response.put("valor", valor);
            response.put("score", score);
            response.put("aprovado", aprovado);
            response.put("mensagem", aprovado ? "Solicitação aprovada" : "Solicitação reprovada");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao analisar solicitação", e);
            Map<String, Object> error = new HashMap<>();
            error.put("erro", "Erro ao processar solicitação");
            error.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/processar-lote")
    public ResponseEntity<Map<String, String>> processarLote(@RequestBody List<String> clientes) {
        log.info("Recebida requisição para processar lote com {} clientes", clientes.size());

        try {
            analiseCreditoService.processarLote(clientes);

            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Lote processado com sucesso");
            response.put("totalClientes", String.valueOf(clientes.size()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao processar lote", e);
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao processar lote");
            error.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

//    @GetMapping("/por-cliente/{cliente}")
//    public ResponseEntity<List<SolicitacaoCredito>> obterSolicitacoesPorCliente(@PathVariable String cliente) {
//        log.info("Buscando solicitações para cliente: {}", cliente);
//        List<SolicitacaoCredito> solicitacoes = analiseCreditoService.obterSolicitacoesPorCliente(cliente);
//        return ResponseEntity.ok(solicitacoes);
//    }
//
//    @GetMapping("/aprovadas")
//    public ResponseEntity<List<SolicitacaoCredito>> obterSolicitacoesAprovadas() {
//        log.info("Buscando solicitações aprovadas");
//        List<SolicitacaoCredito> solicitacoes = analiseCreditoService.obterSolicitacoesAprovadas();
//        return ResponseEntity.ok(solicitacoes);
//    }
//
//    @GetMapping("/reprovadas")
//    public ResponseEntity<List<SolicitacaoCredito>> obterSolicitacoesReprovadas() {
//        log.info("Buscando solicitações reprovadas");
//        List<SolicitacaoCredito> solicitacoes = analiseCreditoService.obterSolicitacoesReprovadas();
//        return ResponseEntity.ok(solicitacoes);
//    }
//
//    @GetMapping("/total-aprovados/{tipoConta}")
//    public ResponseEntity<Map<String, Long>> obterTotalAprovadosPorTipo(@PathVariable String tipoConta) {
//        log.info("Contando solicitações aprovadas para tipo: {}", tipoConta);
//        Long total = analiseCreditoService.obterTotalAprovadosPorTipo(tipoConta);
//
//        Map<String, Long> response = new HashMap<>();
//        response.put("tipoConta", Long.valueOf(tipoConta.length())); // Apenas para exemplo
//        response.put("totalAprovados", total);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/por-periodo")
//    public ResponseEntity<List<SolicitacaoCredito>> obterSolicitacoesPorPeriodo(
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
//            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
//        log.info("Buscando solicitações entre {} e {}", inicio, fim);
//        List<SolicitacaoCredito> solicitacoes = analiseCreditoService.obterSolicitacoesPorPeriodo(inicio, fim);
//        return ResponseEntity.ok(solicitacoes);
//    }

    @GetMapping("/saude")
    public ResponseEntity<Map<String, String>> saude() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("mensagem", "Aplicação funcionando corretamente");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> analiseCredito(@RequestBody List<SolicitacaoCreditoRecord> solicitacaoCreditoRecords) {
        try {
            processadorAnaliseCreditoService.processarLote(solicitacaoCreditoRecords);
            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Solicitações processadas com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao processar solicitações", e);
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao processar solicitações");
            error.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}

