package br.com.nogueiranogueira.aularefatoracao;

import br.com.nogueiranogueira.aularefatoracao.model.SolicitacaoCredito;
import br.com.nogueiranogueira.aularefatoracao.repository.SolicitacaoCreditoRepository;
import br.com.nogueiranogueira.aularefatoracao.service.AnaliseCreditoService;
import br.com.nogueiranogueira.aularefatoracao.service.ServicoAnaliseRisco;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import br.com.nogueiranogueira.aularefatoracao.model.constantes.DiaSemana;
import br.com.nogueiranogueira.aularefatoracao.factory.ValidadorDocumentoFactory;
import br.com.nogueiranogueira.aularefatoracao.strategy.documento.ValidadorDocumentoStrategy;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Testes unitários de AnaliseCreditoService.
 *
 * Todas as dependências são mockadas para isolar o comportamento do serviço.
 * ValidadorDocumento e ServicoAnaliseRisco retornam sucesso por padrão;
 * cada teste sobrescreve apenas o que precisa validar.
 */
public class TestAnaliseCreditoService {

    // CPF e CNPJ com dígitos verificadores corretos
    private static final String CPF_VALIDO  = "529.982.247-25";
    private static final String CNPJ_VALIDO = "11.222.333/0001-81";
    private static final String DOC_INVALIDO = "000.000.000-00";

    private AnaliseCreditoService service;

    private SolicitacaoCreditoRepository repository;
    private ServicoAnaliseRisco servicoAnaliseRisco;
    private MockedStatic<DiaSemana> mockDiaSemana;
    private MockedStatic<ValidadorDocumentoFactory> mockValidadorFactory;

    @Before
    public void setup() {
        repository          = Mockito.mock(SolicitacaoCreditoRepository.class);
        servicoAnaliseRisco = Mockito.mock(ServicoAnaliseRisco.class);

        service = new AnaliseCreditoService(repository, servicoAnaliseRisco);

        // Padrão: documento válido e bureau aprova — cada teste altera o que precisar
        Mockito.when(servicoAnaliseRisco.avaliarCredito(any())).thenReturn(true);
        
        mockDiaSemana = Mockito.mockStatic(DiaSemana.class);
        mockDiaSemana.when(DiaSemana::isFinaldeSemana).thenReturn(false);

        ValidadorDocumentoStrategy dummyValid = doc -> true;
        mockValidadorFactory = Mockito.mockStatic(ValidadorDocumentoFactory.class);
        mockValidadorFactory.when(() -> ValidadorDocumentoFactory.obter(anyString()))
                .thenReturn(Optional.of(dummyValid));
    }

    @After
    public void tearDown() {
        if (mockDiaSemana != null) {
            mockDiaSemana.close();
        }
        if (mockValidadorFactory != null) {
            mockValidadorFactory.close();
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Validações de entrada
    // ──────────────────────────────────────────────────────────────────

    @Test
    public void testDocumentoInvalidoDeveReprovar() {
        ValidadorDocumentoStrategy dummyInvalid = doc -> false;
        mockValidadorFactory.when(() -> ValidadorDocumentoFactory.obter(DOC_INVALIDO))
                .thenReturn(Optional.of(dummyInvalid));

        boolean resultado = service.analisarSolicitacao(DOC_INVALIDO, "João Silva", 3000.0, 700, false, "PF");

        assertFalse("Documento inválido deve ser reprovado", resultado);
    }

    @Test
    public void testValorNegativoDeveReprovar() {
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "João Silva", -1000.0, 700, false, "PF");

        assertFalse("Valor negativo deve ser reprovado", resultado);
    }

    @Test
    public void testValorZeroDeveReprovar() {
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "João Silva", 0.0, 700, false, "PF");

        assertFalse("Valor zero deve ser reprovado", resultado);
    }

    @Test
    public void testClienteNegativadoDeveReprovar() {
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "Maria Santos", 1000.0, 700, true, "PF");

        assertFalse("Cliente negativado deve ser reprovado", resultado);
    }

    @Test
    public void testScoreBaixoDeveReprovar() {
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "Pedro Costa", 1000.0, 400, false, "PF");

        assertFalse("Score abaixo de 500 deve ser reprovado", resultado);
    }

    @Test
    public void testScoreExatamente500DeveReprovar() {
        // A regra é score > 500, então 500 exato cai na reprovação
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "Pedro Costa", 1000.0, 500, false, "PF");

        assertFalse("Score exatamente 500 (não maior que) deve ser reprovado", resultado);
    }

    @Test
    public void testTipoContaInvalidoDeveReprovar() {
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "Cliente X", 1000.0, 700, false, "ME");

        assertFalse("Tipo de conta desconhecido deve ser reprovado", resultado);
    }

    // ──────────────────────────────────────────────────────────────────
    // Bureau externo via Adapter
    // ──────────────────────────────────────────────────────────────────

    @Test
    public void testBureauReprovandoDeveReprovar() {
        Mockito.when(servicoAnaliseRisco.avaliarCredito(any())).thenReturn(false);

        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "João Silva", 3000.0, 700, false, "PF");

        assertFalse("Reprovação pelo bureau externo deve reprovar a solicitação", resultado);
    }

    @Test
    public void testBureauNaoEChamadoQuandoDocumentoEInvalido() {
        ValidadorDocumentoStrategy dummyInvalid = doc -> false;
        mockValidadorFactory.when(() -> ValidadorDocumentoFactory.obter(DOC_INVALIDO))
                .thenReturn(Optional.of(dummyInvalid));

        service.analisarSolicitacao(DOC_INVALIDO, "João Silva", 3000.0, 700, false, "PF");

        // Short-circuit: bureau não deve ser consultado se o documento já falhou
        Mockito.verify(servicoAnaliseRisco, Mockito.never()).avaliarCredito(any());
    }

    // ──────────────────────────────────────────────────────────────────
    // Regras Pessoa Física (PF)
    // ──────────────────────────────────────────────────────────────────

    @Test
    public void testPFValorAltoScoreMedioDeveReprovar() {
        // AnaliseStrategyPF: valor > 5000 exige score > 800
        boolean resultado = service.analisarSolicitacao(CPF_VALIDO, "Ana Costa", 6000.0, 700, false, "PF");

        assertFalse("PF com valor > 5000 e score <= 800 deve ser reprovado", resultado);
    }

    @Test
    public void testPFValorBaixoScoreAdequadoNaoLancaExcecao() {
        // Resultado pode variar por dia da semana (regra de fim de semana na StrategyPF)
        // Garantimos apenas que não explode
        try {
            service.analisarSolicitacao(CPF_VALIDO, "Carlos Silva", 3000.0, 700, false, "PF");
        } catch (Exception e) {
            fail("Não deve lançar exceção: " + e.getMessage());
        }
    }

    // ──────────────────────────────────────────────────────────────────
    // Regras Pessoa Jurídica (PJ)
    // ──────────────────────────────────────────────────────────────────

    @Test
    public void testPJValorAltoScoreBaixoDeveReprovar() {
        boolean resultado = service.analisarSolicitacao(CNPJ_VALIDO, "Empresa XYZ LTDA", 60000.0, 650, false, "PJ");

        assertFalse("PJ com valor > 50000 e score < 700 deve ser reprovado", resultado);
    }

    @Test
    public void testPJValorAltoScoreAltoDeveAprovar() {
        boolean resultado = service.analisarSolicitacao(CNPJ_VALIDO, "Tech Solutions LTDA", 60000.0, 750, false, "PJ");

        assertTrue("PJ com valor alto e score alto deve ser aprovado", resultado);
    }

    @Test
    public void testPJValorBaixoScoreAdequadoDeveAprovar() {
        boolean resultado = service.analisarSolicitacao(CNPJ_VALIDO, "Pequena Empresa LTDA", 30000.0, 650, false, "PJ");

        assertTrue("PJ com valor baixo e score > 500 deve ser aprovado", resultado);
    }

    // ──────────────────────────────────────────────────────────────────
    // processarLote
    // ──────────────────────────────────────────────────────────────────

    @Test
    public void testProcessarLoteVazioNaoLancaExcecao() {
        try {
            service.processarLote(List.of());
        } catch (Exception e) {
            fail("Não deve lançar exceção ao processar lote vazio: " + e.getMessage());
        }
    }

    @Test
    public void testProcessarLoteComMultiplosItens() {
        List<SolicitacaoCredito> lote = Arrays.asList(
                buildSolicitacao(CNPJ_VALIDO, "Empresa A",  25000.0, 750, false, "PJ"),
                buildSolicitacao(CPF_VALIDO,  "Pessoa B",    3000.0, 700, false, "PF"),
                buildSolicitacao(CPF_VALIDO,  "Pessoa C",    1000.0, 300, false, "PF")  // score baixo
        );

        try {
            service.processarLote(lote);
        } catch (Exception e) {
            fail("Não deve lançar exceção ao processar lote: " + e.getMessage());
        }
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
