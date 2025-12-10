package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidatorBuilder;

/**
 * Validador para ClientePjDTO
 */
public class ClientePjValidator {

    /**
     * Valida um ClientePjDTO
     *
     * @param cliente Cliente a ser validado
     * @throws ValidationException se algum campo for inválido
     */
    public static void validate(ClientePjDTO cliente) throws ValidationException {
        if (cliente == null) {
            throw new ValidationException("Cliente não pode ser nulo");
        }

        // Validar CNPJ
        ValidatorBuilder.stringValidator("CNPJ")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(cliente.cnpj());

        // Validar Nome Fantasia
        ValidatorBuilder.stringValidator("Nome Fantasia")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(100)
                .build()
                .validate(cliente.nomeFantasia());

        // Validar Razão Social
        ValidatorBuilder.stringValidator("Razão Social")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(150)
                .build()
                .validate(cliente.razaoSocial());

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

