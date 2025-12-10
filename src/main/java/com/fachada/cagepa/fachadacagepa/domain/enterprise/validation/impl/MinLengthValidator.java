package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para verificar tamanho mínimo de string
 */
public class MinLengthValidator implements Validator<String> {

    private final int minLength;
    private final String fieldName;

    public MinLengthValidator(String fieldName, int minLength) {
        this.fieldName = fieldName;
        this.minLength = minLength;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value == null || value.length() < minLength) {
            throw new ValidationException(
                    fieldName + " deve ter no mínimo " + minLength + " caracteres"
            );
        }
    }

    @Override
    public boolean isValid(String value) {
        return value != null && value.length() >= minLength;
    }
}


