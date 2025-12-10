package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para verificar padrão regex em string
 */
public class PatternValidator implements Validator<String> {

    private final String pattern;
    private final String fieldName;

    public PatternValidator(String fieldName, String pattern) {
        this.fieldName = fieldName;
        this.pattern = pattern;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value != null && !value.matches(pattern)) {
            throw new ValidationException(
                    fieldName + " não corresponde ao padrão esperado: " + pattern
            );
        }
    }

    @Override
    public boolean isValid(String value) {
        return value == null || value.matches(pattern);
    }
}

