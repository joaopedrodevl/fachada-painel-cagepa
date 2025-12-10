package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation;

/**
 * Exceção customizada para validações
 * Usada quando um valor não passa na validação
 */
public class ValidationException extends Exception {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

