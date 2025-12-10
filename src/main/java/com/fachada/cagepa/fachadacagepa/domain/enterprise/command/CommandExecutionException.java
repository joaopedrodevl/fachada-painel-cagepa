package com.fachada.cagepa.fachadacagepa.domain.enterprise.command;

/**
 * Exceção para falhas na execução de comandos
 */
public class CommandExecutionException extends Exception {

    public CommandExecutionException(String message) {
        super(message);
    }

    public CommandExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

