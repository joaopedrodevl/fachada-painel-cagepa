package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.AdminDTO;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.dto.AdminValidator;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Admin;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IAdminJpaRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AdminService {

    private final IAdminJpaRepository adminJpaRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AdminValidator adminValidator;

    private final AuditService auditService;

    public AdminService(IAdminJpaRepository adminJpaRepository,
                        BCryptPasswordEncoder passwordEncoder, AuditService auditService) {
        this.adminJpaRepository = adminJpaRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminValidator = new AdminValidator();
        this.auditService = auditService;
    }

    /**
     * Cria um novo administrador
     * @param username username do novo admin
     * @param password password do novo admin
     * @return Admin criado
     * @throws ValidationException se dados inválidos
     */
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

            // Log de auditoria
            if (auditService != null) {
                auditService.logAdminCreated(username);
            }

            return admin;
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Desativa um administrador
     * @param adminId ID do admin a desativar
     * @return true se desativado com sucesso
     * @throws IllegalArgumentException se admin não encontrado
     */
    @Transactional
    public boolean desativarAdmin(UUID adminId) throws IllegalArgumentException {
        try {
            var admin = adminJpaRepository.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado com ID: " + adminId));

            adminJpaRepository.delete(admin);

            // Log de auditoria
            if (auditService != null) {
                auditService.logAdminDeactivated(admin.getUsername());
            }

            System.out.println("Admin " + admin.getUsername() + " desativado com sucesso");
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desativar admin: " + e.getMessage(), e);
        }
    }

    /**
     * Desativa um administrador pelo username
     * @param username username do admin a desativar
     * @return true se desativado com sucesso
     * @throws IllegalArgumentException se admin não encontrado
     */
    @Transactional
    public boolean desativarAdminPorUsername(String username) throws IllegalArgumentException {
        try {
            var admin = adminJpaRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado com username: " + username));

            adminJpaRepository.delete(admin);

            // Log de auditoria
            if (auditService != null) {
                auditService.logAdminDeactivated(username);
            }

            System.out.println("Admin " + username + " desativado com sucesso");
            return true;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desativar admin: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna lista de todos os administradores cadastrados
     * @return Lista de admins
     */
    @Transactional(readOnly = true)
    public List<Admin> listarAdmins() {
        try {
            return adminJpaRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao listar administradores: " + e.getMessage(), e);
        }
    }

    /**
     * Conta total de administradores
     * @return Número total de admins
     */
    @Transactional(readOnly = true)
    public long contarAdmins() {
        return adminJpaRepository.count();
    }

    /**
     * Obtém admin por username
     * @param username username do admin
     * @return Admin se encontrado
     */
    @Transactional(readOnly = true)
    public Admin obterAdminPorUsername(String username) throws IllegalArgumentException {
        return adminJpaRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Admin não encontrado com username: " + username));
    }
}


