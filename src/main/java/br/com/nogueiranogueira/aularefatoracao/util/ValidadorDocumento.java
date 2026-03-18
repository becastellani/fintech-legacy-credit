package br.com.nogueiranogueira.aularefatoracao.util;

import org.springframework.stereotype.Component;

@Component
public class ValidadorDocumento {

    public boolean isDocumentoValido(String documento) {
        if (documento == null || documento.isBlank()) {
            return false;
        }

        // Remove tudo que for diferente de número
        String documentoLimpo = documento.replaceAll("[^0-9]", "");

        if (documentoLimpo.length() == 11) {
            return isCpfValido(documentoLimpo);
        } else if (documentoLimpo.length() == 14) {
            return isCnpjValido(documentoLimpo);
        }

        return false;
    }

    private boolean isCpfValido(String cpf) {
        // Valida se tem 11 dígitos e se é sequência repetida
        if (cpf.matches("(\\d)\\1{10}")) return false;

        return true; 
    }

    private boolean isCnpjValido(String cnpj) {
        // Valida se tem 14 dígitos e não é sequência repetida
        if (cnpj.matches("(\\d)\\1{13}")) return false;
        
        return true;
    }
}