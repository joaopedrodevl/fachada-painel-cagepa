package com.fachada.cagepa.fachadacagepa.domain.enterprise.command;

import java.time.LocalDateTime;
import java.util.Stack;

/**
 * Invocador do padrão Command
 * Responsável por:
 * - Executar comandos
 * - Manter histórico para auditoria
 * - Implementar undo/redo
 */
public class CommandInvoker {
    
    private final Stack<Command> executedCommands = new Stack<>();
    private final Stack<Command> undoneCommands = new Stack<>();
    
    /**
     * Executa um comando e registra para auditoria
     */
    public void execute(Command command) throws Exception {
        try {
            command.execute();
            executedCommands.push(command);
            undoneCommands.clear(); // Limpa redo quando novo comando é executado
            
            // Registra na auditoria
            logCommandExecution(command, "EXECUTADO");
        } catch (Exception e) {
            logCommandExecution(command, "FALHOU: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Desfaz o último comando executado
     */
    public void undo() throws Exception {
        if (!executedCommands.isEmpty()) {
            Command command = executedCommands.pop();
            try {
                command.undo();
                undoneCommands.push(command);
                logCommandExecution(command, "DESFEITO");
            } catch (Exception e) {
                logCommandExecution(command, "FALHOU AO DESFAZER: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * Refaz o último comando desfeito
     */
    public void redo() throws Exception {
        if (!undoneCommands.isEmpty()) {
            Command command = undoneCommands.pop();
            try {
                command.execute();
                executedCommands.push(command);
                logCommandExecution(command, "REFEITO");
            } catch (Exception e) {
                logCommandExecution(command, "FALHOU AO REFAZER: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * Retorna histórico de comandos executados
     */
    public Stack<Command> getHistory() {
        Stack<Command> history = new Stack<>();
        history.addAll(executedCommands);
        return history;
    }
    
    /**
     * Registra execução do comando para auditoria
     */
    private void logCommandExecution(Command command, String status) {
        String log = String.format("[%s] %s | Usuario: %s | Comando: %s",
            LocalDateTime.now(),
            status,
            command.getExecutor(),
            command.getDescription());
        System.out.println(log);
    }
    
    /**
     * Limpa histórico
     */
    public void clearHistory() {
        executedCommands.clear();
        undoneCommands.clear();
    }
}


