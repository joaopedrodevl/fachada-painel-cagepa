package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Permite compor múltiplos validadores em uma única validação
 *
 * @param <T> Tipo de dado a ser validado
 */
public class CompositeValidator<T> implements Validator<T> {

    private final List<Validator<T>> validators;

    public CompositeValidator() {
        this.validators = new ArrayList<>();
    }

    /**
     * Adiciona um validador à composição
     *
     * @param validator Validador a ser adicionado
     * @return this para method chaining
     */
    public CompositeValidator<T> addValidator(Validator<T> validator) {
        validators.add(validator);
        return this;
    }

    /**
     * Remove um validador da composição
     *
     * @param validator Validador a ser removido
     * @return true se removido, false caso contrário
     */
    public boolean removeValidator(Validator<T> validator) {
        return validators.remove(validator);
    }

    /**
     * Executa todos os validadores. Para na primeira falha.
     *
     * @param value Valor a ser validado
     * @throws ValidationException se algum validador falhar
     */
    @Override
    public void validate(T value) throws ValidationException {
        for (Validator<T> validator : validators) {
            validator.validate(value);
        }
    }

    /**
     * Verifica se o valor passa em todas as validações
     *
     * @param value Valor a ser validado
     * @return true se válido em todas as validações
     */
    @Override
    public boolean isValid(T value) {
        return validators.stream().allMatch(v -> v.isValid(value));
    }

    /**
     * Retorna a quantidade de validadores
     *
     * @return quantidade de validadores
     */
    public int getValidatorCount() {
        return validators.size();
    }
}

