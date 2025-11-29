package com.cagepa.painel.fachada_painel_cagepa.domain.application.exceptions;

public class DadosInvalidosException extends ProcessamentoException {
    public DadosInvalidosException() {
        super("Dados inválidos fornecidos.");
    }
}
