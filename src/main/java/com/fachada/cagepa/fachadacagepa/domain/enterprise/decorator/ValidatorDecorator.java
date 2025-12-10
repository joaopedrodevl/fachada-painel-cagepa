package com.fachada.cagepa.fachadacagepa.domain.enterprise.decorator;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Permite adicionar funcionalidades a validadores sem modificá-los
 */
public abstract class ValidatorDecorator<T> implements Validator<T> {

    protected Validator<T> wrappedValidator;

    public ValidatorDecorator(Validator<T> validator) {
        this.wrappedValidator = validator;
    }

    @Override
    public void validate(T value) throws ValidationException {
        wrappedValidator.validate(value);
    }

    @Override
    public boolean isValid(T value) {
        return wrappedValidator.isValid(value);
    }
}

