package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.AdminDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.AdminValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Admin;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IAdminJpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    private final IAdminJpaRepository adminJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AdminValidator adminValidator;

    public AdminService(IAdminJpaRepository adminJpaRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.adminJpaRepository = adminJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminValidator = new AdminValidator();
    }

    @Transactional
    public Admin createAdmin(String username, String password) throws ValidationException {
        // Validar username e password com AdminValidator
        AdminDTO adminDTO = new AdminDTO(username, password);
        adminValidator.validate(adminDTO);

        try {
            var existingAdmin = adminJpaRepository.findByUsername(username);

            if (existingAdmin.isPresent()) {
                throw new ValidationException("Admin com esse username já existe.");
            }

            var admin = new Admin();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            adminJpaRepository.save(admin);

            return admin;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


