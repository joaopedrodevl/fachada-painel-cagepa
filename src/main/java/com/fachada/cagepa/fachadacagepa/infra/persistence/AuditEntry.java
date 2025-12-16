package com.fachada.cagepa.fachadacagepa.infra.persistence;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.LoginAudit;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.OperacaoAudit;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.EntidadeAudit;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@AllArgsConstructor
@NoArgsConstructor
public class AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginAudit eventType;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "operacao")
    @Enumerated(EnumType.STRING)
    private OperacaoAudit operacao;

    @Column(name = "entidade")
    @Enumerated(EnumType.STRING)
    private EntidadeAudit entidade;

    @Column(name = "detalhes", columnDefinition = "TEXT")
    private String detalhes;

    @Column(name = "resultado", length = 20)
    private String resultado;

    public AuditEntry(LoginAudit eventType, String username, LocalDateTime timestamp) {
        this.eventType = eventType;
        this.username = username;
        this.timestamp = timestamp;
    }

    public AuditEntry(OperacaoAudit operacao, EntidadeAudit entidade, String username, String detalhes, String resultado) {
        this.eventType = LoginAudit.ADMIN_CREATED; 
        this.operacao = operacao;
        this.entidade = entidade;
        this.username = username;
        this.detalhes = detalhes;
        this.resultado = resultado;
        this.timestamp = LocalDateTime.now();
    }
}
