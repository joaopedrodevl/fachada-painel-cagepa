package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation;

/**
 * Classe utilitária para capturar e formatar erros de validação
 * Usada como handler centralizado para exceções de validação
 */
public class ValidationErrorHandler {

    /**
     * Formata uma mensagem de erro de validação
     *
     * @param fieldName Nome do campo
     * @param errorMessage Mensagem de erro
     * @return String formatada
     */
    public static String formatError(String fieldName, String errorMessage) {
        return String.format("Erro em %s: %s", fieldName, errorMessage);
    }

    /**
     * Extrai a mensagem raiz de uma ValidationException
     *
     * @param exception Exceção a processar
     * @return Mensagem limpa
     */
    public static String extractMessage(ValidationException exception) {
        String message = exception.getMessage();
        return message != null ? message.trim() : "Erro desconhecido na validação";
    }

    /**
     * Formata múltiplas validações
     *
     * @param exceptions Lista de exceções
     * @return String formatada com todas as mensagens
     */
    public static String formatMultipleErrors(java.util.List<ValidationException> exceptions) {
        StringBuilder sb = new StringBuilder();
        sb.append("Erros de validação encontrados:\n");
        for (int i = 0; i < exceptions.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, extractMessage(exceptions.get(i))));
        }
        return sb.toString();
    }
}

