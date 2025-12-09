package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Admin;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IAdminJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final IAdminJpaRepository adminRepository;
    private final JwtService jwtService;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    private final AuditService auditService;

    public AuthService(IAdminJpaRepository adminRepository,
                       JwtService jwtService,
                       BCryptPasswordEncoder passwordEncoder, AuditService auditService) {
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public void initializeDefaultAdmin() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            adminRepository.save(admin);
            auditService.logAdminCreated("admin");
            System.out.println("Administrador padrão criado: admin / 123456");
        }
    }

    public String login(String username, String rawPassword) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (passwordEncoder.matches(rawPassword, admin.getPassword())) {
                auditService.logLoginSuccess(username);
                return jwtService.generateToken(username);
            }
        }
        auditService.logLoginFailure(username != null ? username : "unknown");
        return null;
    }

    public boolean isAuthenticated(String token) {
        return jwtService.validateToken(token);
    }
}
