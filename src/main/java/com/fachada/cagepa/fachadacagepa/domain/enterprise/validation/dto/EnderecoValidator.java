package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidatorBuilder;

/**
 * Validador para EnderecoDTO
 */
public class EnderecoValidator {

    /**
     * Valida um EnderecoDTO
     *
     * @param endereco Endereço a ser validado
     * @throws ValidationException se algum campo for inválido
     */
    public static void validate(EnderecoDTO endereco) throws ValidationException {
        if (endereco == null) {
            throw new ValidationException("Endereço não pode ser nulo");
        }

        // Validar Logradouro
        ValidatorBuilder.stringValidator("Logradouro")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(100)
                .build()
                .validate(endereco.logradouro());

        // Validar Número
        ValidatorBuilder.stringValidator("Número")
                .notNullOrEmpty()
                .maxLength(10)
                .build()
                .validate(endereco.numero());

        // Validar Complemento (opcional)
        if (endereco.complemento() != null && !endereco.complemento().isEmpty()) {
            ValidatorBuilder.stringValidator("Complemento")
                    .maxLength(100)
                    .build()
                    .validate(endereco.complemento());
        }

        // Validar Bairro
        ValidatorBuilder.stringValidator("Bairro")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(50)
                .build()
                .validate(endereco.bairro());

        // Validar Cidade
        ValidatorBuilder.stringValidator("Cidade")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(50)
                .build()
                .validate(endereco.cidade());

        // Validar Estado
        ValidatorBuilder.stringValidator("Estado")
                .notNullOrEmpty()
                .minLength(2)
                .maxLength(2)
                .build()
                .validate(endereco.estado());

        // Validar CEP
        ValidatorBuilder.stringValidator("CEP")
                .notNullOrEmpty()
                .minLength(5)
                .maxLength(10)
                .build()
                .validate(endereco.cep());

        // Validar TipoEndereco
        if (endereco.tipoEndereco() == null) {
            throw new ValidationException("Tipo de Endereço não pode ser nulo");
        }
    }
}

