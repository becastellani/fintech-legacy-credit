package br.com.nogueiranogueira.aularefatoracao.controller;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.service.AnaliseCreditoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitacoes")
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoCreditoController {

    @Autowired
    private final AnaliseCreditoService analiseCreditoService;

    @PostMapping("/analisar")
    public ResponseEntity<Map<String, Object>> analisarSolicitacao(
            @RequestParam String documento,
            @RequestParam String cliente,
            @RequestParam Double valor,
            @RequestParam Integer score,
            @RequestParam(defaultValue = "false") Boolean negativado,
            @RequestParam(defaultValue = "PF") String tipoConta) {

        log.info("Recebida requisição de análise para cliente: {} com documento: {}", cliente, documento);

        try {

            boolean aprovado = analiseCreditoService.analisarSolicitacao(documento, cliente, valor, score, negativado, tipoConta);

            Map<String, Object> response = new HashMap<>();
            response.put("documento", documento);
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
    public ResponseEntity<Map<String, String>> processarLote(@RequestBody List<SolicitacaoCredito> solicitacoes) {
        log.info("Recebida requisição para processar lote com {} solicitações", solicitacoes.size());

        try {

            analiseCreditoService.processarLote(solicitacoes);

            Map<String, String> response = new HashMap<>();
            response.put("mensagem", "Lote processado com sucesso");
            response.put("totalSolicitacoes", String.valueOf(solicitacoes.size()));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao processar lote", e);
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Erro ao processar lote");
            error.put("mensagem", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/por-cliente/{cliente}")
    public ResponseEntity<List<SolicitacaoCredito>> obterSolicitacoesPorCliente(@PathVariable String cliente) {
        log.info("Buscando solicitações para cliente: {}", cliente);
        List<SolicitacaoCredito> solicitacoes = analiseCreditoService.obterSolicitacoesPorCliente(cliente);
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/aprovadas")
    public ResponseEntity<List<SolicitacaoCredito>> obterSolicitacoesAprovadas() {
        log.info("Buscando solicitações aprovadas");
        List<SolicitacaoCredito> solicitacoes = analiseCreditoService.obterSolicitacoesAprovadas();
        return ResponseEntity.ok(solicitacoes);
    }

    @GetMapping("/saude")
    public ResponseEntity<Map<String, String>> saude() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("mensagem", "Aplicação funcionando corretamente");
        return ResponseEntity.ok(response);
    }
}