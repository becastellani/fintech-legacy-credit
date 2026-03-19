package br.com.nogueiranogueira.aularefatoracao;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.repository.SolicitacaoCreditoRepository;
import br.com.nogueiranogueira.aularefatoracao.service.AnaliseCreditoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SolicitacaoCreditoIntegrationTest {

    private static final String CPF_VALIDO  = "529.982.247-25";
    private static final String CNPJ_VALIDO = "11.222.333/0001-81";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AnaliseCreditoService analiseCreditoService;

    @Autowired
    private SolicitacaoCreditoRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void testAnalisarSolicitacaoAprovadaPF() throws Exception {
        mockMvc.perform(post("/api/solicitacoes/analisar")
                .param("documento", CPF_VALIDO)
                .param("cliente", "João Silva")
                .param("valor", "3000")
                .param("score", "700")
                .param("negativado", "false")
                .param("tipoConta", "PF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente").value("João Silva"))
                .andExpect(jsonPath("$.valor").value(3000.0));
    }

    @Test
    public void testAnalisarSolicitacaoReprovadaValorInvalido() throws Exception {
        mockMvc.perform(post("/api/solicitacoes/analisar")
                .param("documento", CPF_VALIDO)
                .param("cliente", "Maria Santos")
                .param("valor", "-1000")
                .param("score", "700")
                .param("negativado", "false")
                .param("tipoConta", "PF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aprovado").value(false));
    }

    @Test
    public void testAnalisarSolicitacaoClienteNegativado() throws Exception {
        mockMvc.perform(post("/api/solicitacoes/analisar")
                .param("documento", CPF_VALIDO)
                .param("cliente", "Pedro Costa")
                .param("valor", "5000")
                .param("score", "700")
                .param("negativado", "true")
                .param("tipoConta", "PF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aprovado").value(false));
    }

    @Test
    public void testProcessarLoteViaHTTP() throws Exception {
        SolicitacaoCredito s1 = buildSolicitacao(CNPJ_VALIDO, "Empresa A", 25000.0, 750, false, "PJ");
        SolicitacaoCredito s2 = buildSolicitacao(CPF_VALIDO,  "Pessoa B",   3000.0, 700, false, "PF");

        mockMvc.perform(post("/api/solicitacoes/processar-lote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(s1, s2))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value("Lote processado com sucesso"))
                .andExpect(jsonPath("$.totalSolicitacoes").value("2"));
    }

    @Test
    public void testObterSolicitacoesAprovadas() throws Exception {
        analiseCreditoService.analisarSolicitacao(CPF_VALIDO, "Cliente Aprovado", 2000, 700, false, "PF");

        mockMvc.perform(get("/api/solicitacoes/aprovadas"))
                .andExpect(status().isOk());
    }

    @Test
    public void testObterSolicitacoesPorCliente() throws Exception {
        analiseCreditoService.analisarSolicitacao(CPF_VALIDO, "João", 2000, 700, false, "PF");

        mockMvc.perform(get("/api/solicitacoes/por-cliente/João"))
                .andExpect(status().isOk());
    }

    @Test
    public void testEndpointSaude() throws Exception {
        mockMvc.perform(get("/api/solicitacoes/saude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.mensagem").value("Aplicação funcionando corretamente"));
    }

    @Test
    public void testAnalisarSolicitacaoAprovadaPJComParametrosValidos() {
        boolean resultado = analiseCreditoService.analisarSolicitacao(
                CNPJ_VALIDO, "Empresa ABC LTDA", 25000, 750, false, "PJ");

        assertTrue(resultado, "PJ com valor adequado e score alto deve ser aprovado");

        List<SolicitacaoCredito> solicitacoes = repository.findByCliente("Empresa ABC LTDA");
        assertEquals(1, solicitacoes.size());
        assertTrue(solicitacoes.getFirst().getAprovado());
    }

    @Test
    public void testAnalisarSolicitacaoReprovadaPJComRisco() {
        boolean resultado = analiseCreditoService.analisarSolicitacao(
                CNPJ_VALIDO, "Empresa XYZ LTDA", 60000, 650, false, "PJ");

        assertFalse(resultado, "PJ com valor alto e score baixo deve ser reprovado");

        List<SolicitacaoCredito> solicitacoes = repository.findByCliente("Empresa XYZ LTDA");
        assertEquals(1, solicitacoes.size());
        assertFalse(solicitacoes.getFirst().getAprovado());
        assertNotNull(solicitacoes.getFirst().getMotivoReprovacao());
    }

    @Test
    public void testProcessarLoteComMultiplosClientes() {
        List<SolicitacaoCredito> lote = List.of(
                buildSolicitacao(CNPJ_VALIDO, "Empresa 1", 25000.0, 750, false, "PJ"),
                buildSolicitacao(CPF_VALIDO,  "Pessoa 2",   3000.0, 700, false, "PF"),
                buildSolicitacao(CPF_VALIDO,  "Pessoa 3",   1000.0, 300, false, "PF")
        );
        analiseCreditoService.processarLote(lote);

        assertEquals(3, repository.count(), "Deve haver 3 registros após processar lote");
    }

    // ──────────────────────────────────────────────────────────────────
    // Helper
    // ──────────────────────────────────────────────────────────────────

    private SolicitacaoCredito buildSolicitacao(String documento, String cliente,
                                                double valor, int score,
                                                boolean negativado, String tipoConta) {
        SolicitacaoCredito s = new SolicitacaoCredito();
        s.setDocumento(documento);
        s.setCliente(cliente);
        s.setValor(valor);
        s.setScore(score);
        s.setNegativado(negativado);
        s.setTipoConta(tipoConta);
        return s;
    }
}
