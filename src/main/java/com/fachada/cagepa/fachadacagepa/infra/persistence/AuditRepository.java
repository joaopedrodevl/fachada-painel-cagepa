package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.LoginAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntry, Long> {

    /**
     * Encontra entradas de auditoria por usuário ordenadas por timestamp descendente
     */
    List<AuditEntry> findByUsernameOrderByTimestampDesc(String username);

    /**
     * Encontra entradas de auditoria por período de tempo
     */
    List<AuditEntry> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Encontra entradas de auditoria por tipo de evento
     */
    List<AuditEntry> findByEventTypeOrderByTimestampDesc(LoginAudit eventType);

    /**
     * Encontra entradas de auditoria por usuário e período
     */
    List<AuditEntry> findByUsernameAndTimestampBetweenOrderByTimestampDesc(String username, LocalDateTime dataInicio, LocalDateTime dataFim);
}
