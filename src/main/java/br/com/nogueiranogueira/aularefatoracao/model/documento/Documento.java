package br.com.nogueiranogueira.aularefatoracao.model.documento;

public sealed interface Documento permits Cpf, Curp, Ssn {
}