package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation;

/**
 * Interface Strategy para validação de dados
 *
 * @param <T> Tipo de dado a ser validado
 */
public interface Validator<T> {

    /**
     * Valida um valor
     *
     * @param value Valor a ser validado
     * @throws ValidationException se o valor for inválido
     */
    void validate(T value) throws ValidationException;

    /**
     * Verifica se um valor é válido
     *
     * @param value Valor a ser validado
     * @return true se válido, false caso contrário
     */
    boolean isValid(T value);
}

