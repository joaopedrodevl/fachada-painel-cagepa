package com.fachada.cagepa.fachadacagepa.domain.enterprise.command;

/**
 * Encapsula uma requisição como um objeto, permitindo:
 * - Registrar operações para auditoria
 * - Implementar undo/redo
 * - Fila de operações
 * - Execução adiada
 */
public interface Command {

    /**
     * Executa o comando
     * @throws Exception se a operação falhar
     */
    void execute() throws Exception;

    /**
     * Desfaz o comando (undo)
     * @throws Exception se o desfazer falhar
     */
    void undo() throws Exception;

    /**
     * Retorna descrição do comando para auditoria
     */
    String getDescription();

    /**
     * Retorna quem executou o comando
     */
    String getExecutor();
}

