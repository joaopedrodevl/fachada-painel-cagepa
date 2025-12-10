package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para Email
 */
public class EmailValidator implements Validator<String> {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private final String fieldName;

    public EmailValidator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " não pode ser nulo ou vazio");
        }

        if (!value.matches(EMAIL_REGEX)) {
            throw new ValidationException(fieldName + " deve ser um email válido");
        }
    }

    @Override
    public boolean isValid(String value) {
        return value != null && !value.trim().isEmpty() && value.matches(EMAIL_REGEX);
    }
}

