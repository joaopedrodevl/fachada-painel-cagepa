package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.LoginAudit;
import com.fachada.cagepa.fachadacagepa.infra.persistence.AuditEntry;
import com.fachada.cagepa.fachadacagepa.infra.persistence.AuditRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de Auditoria
 * Suporte a auditoria das leituras processadas
 * Rastreabilidade das operações realizadas no sistema: CRUD, Usuário (Admin), Timestamp, Resultado
 */
@Service
public class AuditService {
    private final AuditRepository auditRepository;

    public AuditService(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Log de login bem-sucedido
     */
    @Transactional
    public void logLoginSuccess(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.LOGIN_SUCCESS, username, LocalDateTime.now()));
    }

    /**
     * Log de falha de login
     */
    @Transactional
    public void logLoginFailure(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.LOGIN_FAILURE, username, LocalDateTime.now()));
    }

    /**
     * Log de criação de admin
     */
    @Transactional
    public void logAdminCreated(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.ADMIN_CREATED, username, LocalDateTime.now()));
    }

    /**
     * Log de desativação de admin
     */
    @Transactional
    public void logAdminDeactivated(String username) {
        auditRepository.save(new AuditEntry(null, LoginAudit.ADMIN_DEACTIVATED, username, LocalDateTime.now()));
    }

    /**
     * Log genérico de operação CRUD
     * @param operacao Tipo de operação (CREATE, READ, UPDATE, DELETE)
     * @param entidade Tipo de entidade (CLIENTE, HIDROMETRO, LEITURA, etc)
     * @param detalhes Detalhes da operação
     * @param usuario Usuário que realizou a operação
     * @param resultado Resultado da operação (SUCESSO, FALHA)
     */
    @Transactional
    public void logOperacaoCrud(String operacao, String entidade, String detalhes, String usuario, String resultado) {
        try {
            String descricao = String.format("[%s] %s | %s | Usuário: %s | Resultado: %s",
                    entidade, operacao, detalhes, usuario, resultado);

            auditRepository.save(new AuditEntry(
                    null,
                    LoginAudit.LOGIN_SUCCESS, // Usar um tipo genérico
                    descricao,
                    LocalDateTime.now()
            ));
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }
    }

    /**
     * Log de criação de cliente
     */
    @Transactional
    public void logClienteCriado(String cpfCnpj, String nome, String usuario) {
        logOperacaoCrud("CREATE", "CLIENTE", "CPF/CNPJ: " + cpfCnpj + " | Nome: " + nome, usuario, "SUCESSO");
    }

    /**
     * Log de desativação de cliente
     */
    @Transactional
    public void logClienteDesativado(String cpfCnpj, String usuario) {
        logOperacaoCrud("UPDATE", "CLIENTE", "CPF/CNPJ: " + cpfCnpj + " | Status: INATIVO", usuario, "SUCESSO");
    }

    /**
     * Log de leitura de hidrometro
     */
    @Transactional
    public void logLeituraHidrometro(String idSha, Integer valor, String usuario) {
        logOperacaoCrud("CREATE", "LEITURA_HIDROMETRO", "SHA: " + idSha + " | Valor: " + valor, usuario, "SUCESSO");
    }

    /**
     * Log de criação de hidrometro
     */
    @Transactional
    public void logHidrometroCriado(String idSha, String clienteCpfCnpj, String usuario) {
        logOperacaoCrud("CREATE", "HIDROMETRO", "SHA: " + idSha + " | Cliente: " + clienteCpfCnpj, usuario, "SUCESSO");
    }

    /**
     * Log de alteração de status de hidrometro
     */
    @Transactional
    public void logHidrometroStatusAlterado(String idSha, String novoStatus, String usuario) {
        logOperacaoCrud("UPDATE", "HIDROMETRO", "SHA: " + idSha + " | Novo Status: " + novoStatus, usuario, "SUCESSO");
    }

    /**
     * Log de notificação enviada
     */
    @Transactional
    public void logNotificacaoEnviada(String clienteCpfCnpj, String idSha, String usuario) {
        logOperacaoCrud("CREATE", "NOTIFICACAO", "Cliente: " + clienteCpfCnpj + " | Hidrometro: " + idSha, usuario, "SUCESSO");
    }

    /**
     * Log de erro em operação
     */
    @Transactional
    public void logErroOperacao(String operacao, String entidade, String detalhes, String usuario, String erro) {
        logOperacaoCrud(operacao, entidade, detalhes + " | Erro: " + erro, usuario, "FALHA");
    }

    /**
     * Obtém histórico de auditoria
     * @return Lista de todas as entradas de auditoria
     */
    @Transactional(readOnly = true)
    public List<AuditEntry> obterHistoricoAuditoria() {
        try {
            return auditRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter histórico de auditoria: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém histórico de auditoria filtrado por usuário
     */
    @Transactional(readOnly = true)
    public List<AuditEntry> obterAuditoriaPorusuario(String username) {
        try {
            return auditRepository.findByUsernameOrderByTimestampDesc(username);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter auditoria por usuário: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém histórico de auditoria por período
     */
    @Transactional(readOnly = true)
    public List<AuditEntry> obterAuditoriaRorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        try {
            return auditRepository.findByTimestampBetweenOrderByTimestampDesc(dataInicio, dataFim);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter auditoria por período: " + e.getMessage(), e);
        }
    }

    /**
     * Obtém histórico de auditoria filtrado por tipo de evento
     */
    @Transactional(readOnly = true)
    public List<AuditEntry> obterAuditoriaPorevento(LoginAudit eventType) {
        try {
            return auditRepository.findByEventTypeOrderByTimestampDesc(eventType);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter auditoria por evento: " + e.getMessage(), e);
        }
    }

    /**
     * Conta total de entradas de auditoria
     */
    @Transactional(readOnly = true)
    public long contarAuditorias() {
        return auditRepository.count();
    }
}
