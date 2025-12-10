package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.impl.*;

/**
 * Builder para criar validadores de forma fluente
 *
 * Exemplos:
 * - ValidatorBuilder.stringValidator("nome")
 *     .notNullOrEmpty()
 *     .minLength(3)
 *     .maxLength(100)
 *     .build();
 *
 * - ValidatorBuilder.intValidator("idade")
 *     .minValue(0)
 *     .maxValue(150)
 *     .build();
 */
public class ValidatorBuilder {

    private ValidatorBuilder() {
    }

    /**
     * Cria um builder para validar strings
     *
     * @param fieldName Nome do campo para mensagens de erro
     * @return ValidatorStringBuilder
     */
    public static ValidatorStringBuilder stringValidator(String fieldName) {
        return new ValidatorStringBuilder(fieldName);
    }

    /**
     * Cria um builder para validar inteiros
     *
     * @param fieldName Nome do campo para mensagens de erro
     * @return ValidatorIntBuilder
     */
    public static ValidatorIntBuilder intValidator(String fieldName) {
        return new ValidatorIntBuilder(fieldName);
    }

    /**
     * Builder específico para strings
     */
    public static class ValidatorStringBuilder {

        private final CompositeValidator<String> compositeValidator;
        private final String fieldName;

        public ValidatorStringBuilder(String fieldName) {
            this.fieldName = fieldName;
            this.compositeValidator = new CompositeValidator<>();
        }

        public ValidatorStringBuilder notNullOrEmpty() {
            compositeValidator.addValidator(new NotNullOrEmptyValidator(fieldName));
            return this;
        }

        public ValidatorStringBuilder minLength(int minLength) {
            compositeValidator.addValidator(new MinLengthValidator(fieldName, minLength));
            return this;
        }

        public ValidatorStringBuilder maxLength(int maxLength) {
            compositeValidator.addValidator(new MaxLengthValidator(fieldName, maxLength));
            return this;
        }

        public ValidatorStringBuilder pattern(String pattern) {
            compositeValidator.addValidator(new PatternValidator(fieldName, pattern));
            return this;
        }

        public ValidatorStringBuilder cpfOrCnpj() {
            compositeValidator.addValidator(new CpfCnpjValidValidator(fieldName));
            return this;
        }

        public ValidatorStringBuilder email() {
            compositeValidator.addValidator(new EmailValidator(fieldName));
            return this;
        }

        public Validator<String> build() {
            return compositeValidator;
        }
    }

    /**
     * Builder específico para inteiros
     */
    public static class ValidatorIntBuilder {

        private final CompositeValidator<Integer> compositeValidator;
        private final String fieldName;

        public ValidatorIntBuilder(String fieldName) {
            this.fieldName = fieldName;
            this.compositeValidator = new CompositeValidator<>();
        }

        public ValidatorIntBuilder minValue(int minValue) {
            compositeValidator.addValidator(new MinValueValidator(fieldName, minValue));
            return this;
        }

        public ValidatorIntBuilder maxValue(int maxValue) {
            compositeValidator.addValidator(new MaxValueValidator(fieldName, maxValue));
            return this;
        }

        public Validator<Integer> build() {
            return compositeValidator;
        }
    }
}

