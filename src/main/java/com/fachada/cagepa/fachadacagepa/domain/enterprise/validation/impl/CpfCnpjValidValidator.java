package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;
import com.fachada.cagepa.fachadacagepa.domain.util.CpfCnpjValidator;

/**
 * Validador para CPF/CNPJ
 */
public class CpfCnpjValidValidator implements Validator<String> {

    private final String fieldName;

    public CpfCnpjValidValidator(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " não pode ser nulo ou vazio");
        }

        if (!CpfCnpjValidator.isValidCpfOrCnpj(value)) {
            throw new ValidationException(fieldName + " é inválido");
        }
    }

    @Override
    public boolean isValid(String value) {
        return value != null && !value.trim().isEmpty() && CpfCnpjValidator.isValidCpfOrCnpj(value);
    }
}

