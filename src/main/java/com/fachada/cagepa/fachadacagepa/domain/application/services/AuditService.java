package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.LoginAudit;
import com.fachada.cagepa.fachadacagepa.infra.persistence.AuditEntry;
import com.fachada.cagepa.fachadacagepa.infra.persistence.AuditRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Transactional
    public void logLoginSuccess(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.LOGIN_SUCCESS, username, LocalDateTime.now()));
    }

    @Transactional
    public void logLoginFailure(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.LOGIN_FAILURE, username, LocalDateTime.now()));
    }

    @Transactional
    public void logAdminCreated(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.ADMIN_CREATED, username, LocalDateTime.now()));
    }
}
