package com.fachada.cagepa.fachadacagepa.domain.enterprise.decorator;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;

/**
 * Decorator concreto: Logging
 * Adiciona logging automaticamente a qualquer validador
 * Exemplo: ValidatorBuilder.stringValidator("Email")
 *          .email()
 *          .build()
 *          .decorated(new LoggingValidatorDecorator<>(...))
 */
public class LoggingValidatorDecorator<T> extends ValidatorDecorator<T> {

    public LoggingValidatorDecorator(Validator<T> validator) {
        super(validator);
    }

    @Override
    public void validate(T value) throws ValidationException {
        long startTime = System.currentTimeMillis();
        try {
            super.validate(value);
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Validação bem-sucedida em " + duration + "ms: " + value);
        } catch (ValidationException e) {
            long duration = System.currentTimeMillis() - startTime;
            System.err.println("Validação falhou em " + duration + "ms: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isValid(T value) {
        System.out.println("Verificando se é válido: " + value);
        return super.isValid(value);
    }
}

