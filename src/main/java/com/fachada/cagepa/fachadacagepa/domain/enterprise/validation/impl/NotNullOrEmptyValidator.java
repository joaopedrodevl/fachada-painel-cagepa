package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para verificar se uma string é nula ou vazia
 */
public class NotNullOrEmptyValidator implements Validator<String> {

    private final String fieldName;

    public NotNullOrEmptyValidator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " não pode ser nulo ou vazio");
        }
    }

    @Override
    public boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }
}

