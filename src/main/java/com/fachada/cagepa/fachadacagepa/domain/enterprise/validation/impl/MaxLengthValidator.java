package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para verificar tamanho máximo de string
 */
public class MaxLengthValidator implements Validator<String> {

    private final int maxLength;
    private final String fieldName;

    public MaxLengthValidator(String fieldName, int maxLength) {
        this.fieldName = fieldName;
        this.maxLength = maxLength;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(
                    fieldName + " não deve ter mais de " + maxLength + " caracteres"
            );
        }
    }

    @Override
    public boolean isValid(String value) {
        return value == null || value.length() <= maxLength;
    }
}

