package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidatorBuilder;

/**
 * Validador para HidrometroDTO
 */
public class HidrometroValidator {

    /**
     * Valida um HidrometroDTO
     *
     * @param hidrometro Hidrometro a ser validado
     * @throws ValidationException se algum campo for inválido
     */
    public static void validate(HidrometroDTO hidrometro) throws ValidationException {
        if (hidrometro == null) {
            throw new ValidationException("Hidrometro não pode ser nulo");
        }

        // Validar ID SHA
        ValidatorBuilder.stringValidator("ID SHA")
                .notNullOrEmpty()
                .minLength(3)
                .maxLength(50)
                .build()
                .validate(hidrometro.idSha());

        // Validar Data Instalação
        if (hidrometro.dataInstalacao() == null) {
            throw new ValidationException("Data de Instalação não pode ser nula");
        }

        // Validar Status
        ValidatorBuilder.stringValidator("Status")
                .notNullOrEmpty()
                .build()
                .validate(hidrometro.status());

        // Validar Limite Consumo (deve ser positivo)
        ValidatorBuilder.intValidator("Limite Consumo Mensal")
                .minValue(1)
                .maxValue(100000)
                .build()
                .validate(hidrometro.limiteConsumoMensalM3());

        // Validar CPF/CNPJ do Cliente
        ValidatorBuilder.stringValidator("CPF/CNPJ do Cliente")
                .notNullOrEmpty()
                .cpfOrCnpj()
                .build()
                .validate(hidrometro.clienteCpfCnpj());

        // Validar Endereço
        if (hidrometro.enderecoInstalacao() == null) {
            throw new ValidationException("Endereço de Instalação não pode ser nulo");
        }
        EnderecoValidator.validate(hidrometro.enderecoInstalacao());
    }
}

