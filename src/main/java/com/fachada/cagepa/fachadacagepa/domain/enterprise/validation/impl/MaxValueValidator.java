package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Validador para verificar se um inteiro é menor ou igual a um valor máximo
 */
public class MaxValueValidator implements Validator<Integer> {

    private final int maxValue;
    private final String fieldName;

    public MaxValueValidator(String fieldName, int maxValue) {
        this.fieldName = fieldName;
        this.maxValue = maxValue;
    }

    @Override
    public void validate(Integer value) throws ValidationException {
        if (value != null && value > maxValue) {
            throw new ValidationException(
                    fieldName + " não deve ser maior que " + maxValue
            );
        }
    }

    @Override
    public boolean isValid(Integer value) {
        return value == null || value <= maxValue;
    }
}

