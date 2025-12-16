package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.LoginAudit;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.OperacaoAudit;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.EntidadeAudit;
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
        auditRepository.save(new AuditEntry(LoginAudit.LOGIN_SUCCESS, username, LocalDateTime.now()));
    }

    /**
     * Log de falha de login
     */
    @Transactional
    public void logLoginFailure(String username) {
        auditRepository.save(new AuditEntry(LoginAudit.LOGIN_FAILURE, username, LocalDateTime.now()));
    }

    /**
     * Log de criação de admin
     */
    @Transactional
    public void logAdminCreated(String username) {
        auditRepository.save(new AuditEntry(LoginAudit.ADMIN_CREATED, username, LocalDateTime.now()));
    }

    /**
     * Log de desativação de admin
     */
    @Transactional
    public void logAdminDeactivated(String username) {
        auditRepository.save(new AuditEntry(LoginAudit.ADMIN_DEACTIVATED, username, LocalDateTime.now()));
    }

    /**
     * Log de operação CRUD com rastreabilidade completa
     * @param operacao Tipo de operação (CREATE, READ, UPDATE, DELETE)
     * @param entidade Tipo de entidade (CLIENTE, HIDROMETRO, LEITURA, etc)
     * @param detalhes Detalhes da operação
     * @param usuario Usuário que realizou a operação
     * @param resultado Resultado da operação (SUCESSO, FALHA)
     */
    @Transactional
    public void logOperacaoCrud(OperacaoAudit operacao, EntidadeAudit entidade, String detalhes, String usuario, String resultado) {
        try {
            auditRepository.save(new AuditEntry(operacao, entidade, usuario, detalhes, resultado));
        } catch (Exception e) {
            System.err.println("Erro ao registrar auditoria: " + e.getMessage());
        }
    }

    /**
     * Log de criação de cliente
     */
    @Transactional
    public void logClienteCriado(String cpfCnpj, String nome, String usuario) {
        logOperacaoCrud(OperacaoAudit.CREATE, EntidadeAudit.CLIENTE, 
                "CPF/CNPJ: " + cpfCnpj + " | Nome: " + nome, usuario, "SUCESSO");
    }

    /**
     * Log de leitura de cliente
     */
    @Transactional
    public void logClienteLido(String cpfCnpj, String usuario) {
        logOperacaoCrud(OperacaoAudit.READ, EntidadeAudit.CLIENTE,
                "CPF/CNPJ: " + cpfCnpj, usuario, "SUCESSO");
    }

    /**
     * Log de desativação de cliente
     */
    @Transactional
    public void logClienteDesativado(String cpfCnpj, String usuario) {
        logOperacaoCrud(OperacaoAudit.UPDATE, EntidadeAudit.CLIENTE, 
                "CPF/CNPJ: " + cpfCnpj + " | Status: INATIVO", usuario, "SUCESSO");
    }

    /**
     * Log de leitura de hidrometro
     */
    @Transactional
    public void logLeituraHidrometro(String idSha, Integer valor, String usuario) {
        logOperacaoCrud(OperacaoAudit.CREATE, EntidadeAudit.LEITURA, 
                "SHA: " + idSha + " | Valor: " + valor, usuario, "SUCESSO");
    }

    /**
     * Log de leitura de hidrometro
     */
    @Transactional
    public void logBuscaHidrometroPorSha(String idSha, String usuario) {
        logOperacaoCrud(OperacaoAudit.READ, EntidadeAudit.HIDROMETRO,
                "SHA: " + idSha, usuario, "SUCESSO");
    }

    /**
     * Log de criação de hidrometro
     */
    @Transactional
    public void logHidrometroCriado(String idSha, String clienteCpfCnpj, String usuario) {
        logOperacaoCrud(OperacaoAudit.CREATE, EntidadeAudit.HIDROMETRO, 
                "SHA: " + idSha + " | Cliente: " + clienteCpfCnpj, usuario, "SUCESSO");
    }

    /**
     * Log de alteração de status de hidrometro
     */
    @Transactional
    public void logHidrometroStatusAlterado(String idSha, String novoStatus, String usuario) {
        logOperacaoCrud(OperacaoAudit.UPDATE, EntidadeAudit.HIDROMETRO, 
                "SHA: " + idSha + " | Novo Status: " + novoStatus, usuario, "SUCESSO");
    }

    /**
     * Log de notificação enviada
     */
    @Transactional
    public void logNotificacaoEnviada(String clienteCpfCnpj, String idSha, String usuario) {
        logOperacaoCrud(OperacaoAudit.CREATE, EntidadeAudit.NOTIFICACAO, 
                "Cliente: " + clienteCpfCnpj + " | Hidrometro: " + idSha, usuario, "SUCESSO");
    }

    /**
     * Log de erro em operação
     */
    @Transactional
    public void logErroOperacao(OperacaoAudit operacao, EntidadeAudit entidade, String detalhes, String usuario, String erro) {
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
