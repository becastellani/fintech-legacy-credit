package br.com.nogueiranogueira.aularefatoracao.whitebox;

import org.becastellani.validador.documento.Documento;

/**
 * Módulo White-box — Padrão Template Method.
 *
 * Define o esqueleto fixo do algoritmo de validação financeira:
 *   1. log()     — registra o início da operação
 *   2. auditar() — registra contexto para fins de auditoria
 *   3. executar()— lógica de negócio específica (hook — subclasse decide)
 *
 * Inversão de Controle (IoC) via Template Method:
 *   O framework chama {@code executar()} na subclasse — a subclasse não controla
 *   quando é chamada, apenas COMO responde. Isso é o "Hollywood Principle":
 *   "Don't call us, we'll call you."
 *
 * @see ValidadorPix
 */
public abstract class ValidadorFinanceiroWhiteBox {

    /**
     * Template Method — não pode ser sobrescrito ({@code final}).
     * Orquestra o fluxo completo; subclasses customizam apenas o hook {@link #executar}.
     *
     * @param documento documento de identidade do cliente
     * @param valor     valor da transação financeira
     * @return true se aprovado, false caso contrário
     */
    public final boolean validar(Documento documento, double valor) {
        log("Iniciando validação [" + getClass().getSimpleName() + "] — doc=" + documento + " valor=" + valor);
        auditar(documento, valor);
        boolean resultado = executar(documento, valor);   // <-- IoC: framework chama a subclasse
        log("Resultado: " + (resultado ? "APROVADO" : "REPROVADO"));
        return resultado;
    }

    /**
     * Etapa de log. Pode ser sobrescrita para redirecionar para SLF4J, CloudWatch, etc.
     */
    protected void log(String mensagem) {
        System.out.println("[WHITEBOX][LOG] " + mensagem);
    }

    /**
     * Etapa de auditoria. Pode ser sobrescrita para persistir em banco ou enviar evento.
     */
    protected void auditar(Documento documento, double valor) {
        System.out.println("[WHITEBOX][AUDIT] tipo=" + documento.getClass().getSimpleName()
                + " valor=R$" + String.format("%.2f", valor));
    }

    /**
     * Hook — ponto de extensão obrigatório.
     * Cada subclasse implementa as regras específicas do produto financeiro.
     *
     * @param documento documento validado
     * @param valor     valor da transação
     * @return true se as regras do produto aprovam a operação
     */
    protected abstract boolean executar(Documento documento, double valor);
}
