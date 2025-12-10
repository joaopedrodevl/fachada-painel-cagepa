package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para verificar se um inteiro é maior ou igual a um valor mínimo
 */
public class MinValueValidator implements Validator<Integer> {

    private final int minValue;
    private final String fieldName;

    public MinValueValidator(String fieldName, int minValue) {
        this.fieldName = fieldName;
        this.minValue = minValue;
    }

    @Override
    public void validate(Integer value) throws ValidationException {
        if (value == null || value < minValue) {
            throw new ValidationException(
                    fieldName + " deve ser no mínimo " + minValue
            );
        }
    }

    @Override
    public boolean isValid(Integer value) {
        return value != null && value >= minValue;
    }
}

