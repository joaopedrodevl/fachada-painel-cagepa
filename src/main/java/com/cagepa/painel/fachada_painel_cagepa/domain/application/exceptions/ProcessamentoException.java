package com.cagepa.painel.fachada_painel_cagepa.domain.application.exceptions;

public abstract class ProcessamentoException extends RuntimeException {
    public ProcessamentoException(String message) {
        super(message);
    }
}
