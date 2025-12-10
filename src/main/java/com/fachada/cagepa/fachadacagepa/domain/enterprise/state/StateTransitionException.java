package com.fachada.cagepa.fachadacagepa.domain.enterprise.state;

/**
 * Exceção para transições de estado inválidas
 */
public class StateTransitionException extends Exception {

    public StateTransitionException(String message) {
        super(message);
    }

    public StateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
}

