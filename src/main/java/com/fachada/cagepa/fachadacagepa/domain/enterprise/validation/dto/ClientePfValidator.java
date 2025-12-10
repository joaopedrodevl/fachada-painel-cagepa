package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidatorBuilder;

/**
 * Validador para ClientePfDTO
 */
public class ClientePfValidator {

    /**
     * Valida um ClientePfDTO
     *
     * @param cliente Cliente a ser validado
     * @throws ValidationException se algum campo for inválido
     */
    public static void validate(ClientePfDTO cliente) throws ValidationException {
        if (cliente == null) {
            throw new ValidationException("Cliente não pode ser nulo");
        }

        // Validar CPF
        ValidatorBuilder.stringValidator("CPF")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(cliente.cpf());

        // Validar Nome
        ValidatorBuilder.stringValidator("Nome")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(100)
                .build()
                .validate(cliente.nome());

        // Validar Email
        ValidatorBuilder.stringValidator("Email")
                .notNullOrEmpty()
                .email()
                .maxLength(100)
                .build()
                .validate(cliente.email());

        // Validar Telefone
        ValidatorBuilder.stringValidator("Telefone")
                .notNullOrEmpty()
                .minLength(10)
                .maxLength(15)
                .build()
                .validate(cliente.telefone());

        // Validar Endereço
        if (cliente.endereco() == null) {
            throw new ValidationException("Endereço não pode ser nulo");
        }
        EnderecoValidator.validate(cliente.endereco());
    }
}

