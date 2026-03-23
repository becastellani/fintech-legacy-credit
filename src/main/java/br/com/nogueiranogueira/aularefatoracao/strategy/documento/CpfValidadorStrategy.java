package br.com.nogueiranogueira.aularefatoracao.strategy.documento;

public class CpfValidadorStrategy implements ValidadorDocumentoStrategy {

    @Override
    public boolean validar(String documento) {
        return isCpfValido(documento);
    };

    private boolean isCpfValido(String cpf) {
        // Rejeita sequências triviais como "111.111.111-11"
        if (cpf.matches("(\\d)\\1{10}"))
            return false;

        int primeiroDigito = calcularDigitoCpf(cpf, 9);
        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9)))
            return false;

        int segundoDigito = calcularDigitoCpf(cpf, 10);
        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }

    /**
     * Calcula um dígito verificador de CPF.
     *
     * @param cpf    string de 11 dígitos
     * @param length quantos dígitos usar no cálculo (9 para o 1º, 10 para o 2º)
     */
    private int calcularDigitoCpf(String cpf, int length) {
        int soma = 0;
        for (int i = 0; i < length; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (length + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return (resto == 10 || resto == 11) ? 0 : resto;
    }
}
