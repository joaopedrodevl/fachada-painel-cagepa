package com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.AdminDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.Validator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Validador para Admin - valida username e password
 *
 * Regras:
 * - Username: mínimo 3 caracteres, máximo 50, sem espaços, alfanumérico + underscore
 * - Password: mínimo 8 caracteres, máximo 128, contém maiúscula, minúscula, número e caractere especial
 */
public class AdminValidator implements Validator<AdminDTO> {

    public AdminValidator() {
    }

    @Override
    public void validate(AdminDTO admin) throws ValidationException {
        if (admin == null) {
            throw new ValidationException("Admin não pode ser nulo");
        }

        validateUsername(admin.username());
        validatePassword(admin.password());
    }

    @Override
    public boolean isValid(AdminDTO admin) {
        try {
            validate(admin);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    /**
     * Valida username
     * - Mínimo 3 caracteres
     * - Máximo 50 caracteres
     * - Apenas letras, números e underscore
     * - Não pode ter espaços
     * - Não pode começar com número
     */
    private void validateUsername(String username) throws ValidationException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username não pode ser vazio");
        }

        username = username.trim();

        if (username.length() < 3) {
            throw new ValidationException("Username deve ter mínimo 3 caracteres");
        }

        if (username.length() > 50) {
            throw new ValidationException("Username deve ter máximo 50 caracteres");
        }

        if (username.contains(" ")) {
            throw new ValidationException("Username não pode conter espaços");
        }

        if (!username.matches("^[a-zA-Z][a-zA-Z0-9_]*$")) {
            throw new ValidationException("Username deve começar com letra e conter apenas letras, números e underscore");
        }
    }

    /**
     * Valida password
     * - Mínimo 8 caracteres
     * - Máximo 128 caracteres
     * - Contém pelo menos uma letra maiúscula
     * - Contém pelo menos uma letra minúscula
     * - Contém pelo menos um número
     * - Contém pelo menos um caractere especial
     */
    private void validatePassword(String password) throws ValidationException {
        if (password == null || password.isEmpty()) {
            throw new ValidationException("Password não pode ser vazio");
        }

        if (password.length() < 8) {
            throw new ValidationException("Password deve ter mínimo 8 caracteres");
        }

        if (password.length() > 128) {
            throw new ValidationException("Password deve ter máximo 128 caracteres");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password deve conter pelo menos uma letra maiúscula");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password deve conter pelo menos uma letra minúscula");
        }

        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password deve conter pelo menos um número");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?].*")) {
            throw new ValidationException("Password deve conter pelo menos um caractere especial (!@#$%^&* etc)");
        }
    }
}

