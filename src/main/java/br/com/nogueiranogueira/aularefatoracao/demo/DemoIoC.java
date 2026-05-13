package br.com.nogueiranogueira.aularefatoracao.demo;

import br.com.nogueiranogueira.aularefatoracao.blackbox.MotorValidacaoBlackBox;
import br.com.nogueiranogueira.aularefatoracao.blackbox.RegraValidacaoCreditoDigital;
import br.com.nogueiranogueira.aularefatoracao.blackbox.RegraValidacaoFinanceira;
import br.com.nogueiranogueira.aularefatoracao.blackbox.ValidadorBlackBoxFactory;
import br.com.nogueiranogueira.aularefatoracao.whitebox.ValidadorFinanceiroWhiteBox;
import br.com.nogueiranogueira.aularefatoracao.whitebox.ValidadorPix;
import org.becastellani.validador.documento.Cnpj;
import org.becastellani.validador.documento.Cpf;
import org.becastellani.validador.documento.Documento;

/**
 * Demonstração da Inversão de Controle (IoC) nas abordagens White-box e Black-box.
 *
 * Esta classe é um runner standalone (não depende do Spring Boot) e pode ser executada
 * diretamente pela JVM. Ela instancia e exercita ambas as abordagens, tornando a IoC
 * explícita e observável via saída no console.
 *
 * ────────────────────────────────────────────────────────────────────────────────
 * Onde ocorre a IoC?
 *
 * WHITE-BOX (Template Method):
 *   A IoC ocorre em {@code ValidadorFinanceiroWhiteBox#validar()} — o framework
 *   (classe abstrata) chama {@code executar()} na subclasse. A subclasse não decide
 *   quando é invocada; o framework decide. O controle do fluxo principal pertence
 *   ao framework, não à subclasse.
 *
 * BLACK-BOX (Strategy + Constructor Injection):
 *   A IoC ocorre na linha:
 *     {@code new MotorValidacaoBlackBox(regra)}
 *   O motor não instancia a regra — recebe-a como dependência. Quem controla QUAL
 *   regra é executada é o chamador (esta classe DemoIoC), não o motor.
 * ────────────────────────────────────────────────────────────────────────────────
 */
public class DemoIoC {

    public static void main(String[] args) {
        Documento cpfValido   = new Cpf("529.982.247-25");
        Documento cpfInvalido = new Cpf("111.111.111-11");
        Documento cnpjValido  = new Cnpj("11.222.333/0001-81");

        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║       DEMO IoC — White-box vs Black-box                      ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");

        // ────────────────────────────────────────────────────────────────────
        // ABORDAGEM 1 — White-box: Template Method
        //
        // IoC: ValidadorFinanceiroWhiteBox.validar() chama executar() na subclasse.
        //      O fluxo (log → audit → executar) é controlado pelo framework.
        //      ValidadorPix apenas implementa o hook; não controla quando é chamado.
        // ────────────────────────────────────────────────────────────────────
        System.out.println("\n─── WHITE-BOX: Template Method (IoC via herança) ───────────────");

        ValidadorFinanceiroWhiteBox validadorPix = new ValidadorPix();   // subclasse injetada via polimorfismo

        System.out.println("\n[Caso 1] CPF válido, valor R$500,00 → esperado: APROVADO");
        validadorPix.validar(cpfValido, 500.00);

        System.out.println("\n[Caso 2] CPF inválido, valor R$500,00 → esperado: REPROVADO");
        validadorPix.validar(cpfInvalido, 500.00);

        System.out.println("\n[Caso 3] CPF válido, valor R$25.000,00 (acima limite Pix) → esperado: REPROVADO");
        validadorPix.validar(cpfValido, 25_000.00);

        System.out.println("\n[Caso 4] CNPJ válido, valor R$15.000,00 → esperado: APROVADO");
        validadorPix.validar(cnpjValido, 15_000.00);

        // ────────────────────────────────────────────────────────────────────
        // ABORDAGEM 2 — Black-box: Strategy + Constructor Injection
        //
        // IoC: MotorValidacaoBlackBox recebe a regra injetada no construtor.
        //      O motor não sabe qual produto está validando — apenas executa o contrato.
        //      Quem decide a regra é o chamador (aqui, DemoIoC ou a Factory).
        // ────────────────────────────────────────────────────────────────────
        System.out.println("\n─── BLACK-BOX: Strategy + DI via construtor (IoC via composição) ──");

        // Injeção direta — transparente e testável
        RegraValidacaoFinanceira regra = new RegraValidacaoCreditoDigital();
        MotorValidacaoBlackBox motor   = new MotorValidacaoBlackBox(regra); // ← IoC aqui

        System.out.println("\n[Caso 5] CPF válido, valor R$5.000,00 → esperado: APROVADO");
        motor.executar(cpfValido, 5_000.00);

        System.out.println("\n[Caso 6] CPF inválido, valor R$5.000,00 → esperado: REPROVADO");
        motor.executar(cpfInvalido, 5_000.00);

        System.out.println("\n[Caso 7] CPF válido, valor R$80.000,00 (acima limite) → esperado: REPROVADO");
        motor.executar(cpfValido, 80_000.00);

        // Via Factory — abstrai a montagem para o cliente
        System.out.println("\n─── BLACK-BOX via Factory ─────────────────────────────────────────");
        MotorValidacaoBlackBox motorFactory = ValidadorBlackBoxFactory.criarParaCreditoDigital();

        System.out.println("\n[Caso 8] CNPJ válido, valor R$30.000,00 via Factory → esperado: APROVADO");
        motorFactory.executar(cnpjValido, 30_000.00);

        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║  Demo concluída. IoC demonstrada em ambas as abordagens.    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }
}
