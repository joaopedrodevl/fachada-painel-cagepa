package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao;
import com.fachada.cagepa.fachadacagepa.infra.persistence.INotificacaoJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciar a persistência de notificações
 */
@Service
public class NotificacaoPersistenciaService {

    private final INotificacaoJpaRepository notificacaoJpaRepository;

    public NotificacaoPersistenciaService(INotificacaoJpaRepository notificacaoJpaRepository) {
        this.notificacaoJpaRepository = notificacaoJpaRepository;
    }

    /**
     * Salva uma notificação no banco de dados
     * @param notificacao Notificação a ser salva
     * @return Notificação salva
     */
    @Transactional
    public Notificacao salvarNotificacao(Notificacao notificacao) {
        if (notificacao == null) {
            throw new IllegalArgumentException("Notificação não pode ser nula");
        }

        try {
            return notificacaoJpaRepository.save(notificacao);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao salvar notificação: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna histórico de notificações enviadas para um cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Lista de notificações do cliente
     */
    @Transactional(readOnly = true)
    public List<Notificacao> obterHistoricoNotificacoesCliente(String clienteCpfCnpj) {
        try {
            return notificacaoJpaRepository.findByClienteCpfCnpjOrderByDataEnvioDesc(clienteCpfCnpj);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter histórico de notificações: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna histórico de notificações por período
     * @param dataInicio Data inicial
     * @param dataFim Data final
     * @return Lista de notificações no período
     */
    @Transactional(readOnly = true)
    public List<Notificacao> obterHistoricoNotificacoesPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        try {
            return notificacaoJpaRepository.findByDataEnvioBetweenOrderByDataEnvioDesc(dataInicio, dataFim);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter histórico por período: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna histórico de notificações por hidrometro
     * @param hidrometroIdSha ID SHA do hidrometro
     * @return Lista de notificações do hidrometro
     */
    @Transactional(readOnly = true)
    public List<Notificacao> obterHistoricoNotificacoesHidrometro(String hidrometroIdSha) {
        try {
            return notificacaoJpaRepository.findByHidrometroIdShaOrderByDataEnvioDesc(hidrometroIdSha);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter histórico do hidrometro: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se existe notificação duplicada no mesmo dia
     * Evita enviar notificação duplicada no mesmo dia ao mesmo cliente e ao mesmo hidrômetro
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @param hidrometroIdSha ID SHA do hidrometro
     * @return true se existe notificação no mesmo dia, false caso contrário
     */
    @Transactional(readOnly = true)
    public boolean existeNotificacaoDuplicadaHoje(String clienteCpfCnpj, String hidrometroIdSha) {
        try {
            LocalDate hoje = LocalDate.now();
            LocalDateTime inicioHoje = hoje.atStartOfDay();
            LocalDateTime fimHoje = hoje.plusDays(1).atStartOfDay();

            List<Notificacao> notificacoes = notificacaoJpaRepository.findByClienteCpfCnpjAndHidrometroIdShaAndDataEnvioBetween(
                    clienteCpfCnpj,
                    hidrometroIdSha,
                    inicioHoje,
                    fimHoje
            );

            return !notificacoes.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar notificação duplicada: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica se pode enviar notificação
     * Retorna false se existe notificação duplicada no mesmo dia
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @param hidrometroIdSha ID SHA do hidrometro
     * @return true se pode enviar, false caso contrário
     */
    @Transactional(readOnly = true)
    public boolean podeEnviarNotificacao(String clienteCpfCnpj, String hidrometroIdSha) {
        return !existeNotificacaoDuplicadaHoje(clienteCpfCnpj, hidrometroIdSha);
    }

    /**
     * Obtém a última notificação enviada para um hidrometro de um cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @param hidrometroIdSha ID SHA do hidrometro
     * @return Optional com a última notificação
     */
    @Transactional(readOnly = true)
    public Optional<Notificacao> obterUltimaNotificacao(String clienteCpfCnpj, String hidrometroIdSha) {
        try {
            return notificacaoJpaRepository.findFirstByClienteCpfCnpjAndHidrometroIdShaOrderByDataEnvioDesc(
                    clienteCpfCnpj,
                    hidrometroIdSha
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter última notificação: " + e.getMessage(), e);
        }
    }

    /**
     * Conta total de notificações
     * @return Número total de notificações
     */
    @Transactional(readOnly = true)
    public long contarNotificacoes() {
        try {
            return notificacaoJpaRepository.count();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar notificações: " + e.getMessage(), e);
        }
    }

    /**
     * Conta notificações enviadas com sucesso
     * @return Número de notificações com status ENVIADO
     */
    @Transactional(readOnly = true)
    public long contarNotificacoesEnviadas() {
        try {
            return notificacaoJpaRepository.countByStatusEnvio("ENVIADO");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar notificações enviadas: " + e.getMessage(), e);
        }
    }

    /**
     * Conta notificações com falha
     * @return Número de notificações com status FALHA
     */
    @Transactional(readOnly = true)
    public long contarNotificacoesComFalha() {
        try {
            return notificacaoJpaRepository.countByStatusEnvio("FALHA");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao contar notificações com falha: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza o status de uma notificação
     * @param notificacaoId ID da notificação
     * @param novoStatus Novo status
     */
    @Transactional
    public void atualizarStatusNotificacao(java.util.UUID notificacaoId, String novoStatus) {
        try {
            var notificacao = notificacaoJpaRepository.findById(notificacaoId)
                    .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));

            notificacao.setStatusEnvio(novoStatus);
            notificacaoJpaRepository.save(notificacao);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status da notificação: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza o status e mensagem de erro de uma notificação
     * @param notificacaoId ID da notificação
     * @param novoStatus Novo status
     * @param mensagemErro Mensagem de erro (opcional)
     */
    @Transactional
    public void atualizarStatusNotificacao(java.util.UUID notificacaoId, String novoStatus, String mensagemErro) {
        try {
            var notificacao = notificacaoJpaRepository.findById(notificacaoId)
                    .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada"));

            notificacao.setStatusEnvio(novoStatus);
            if (mensagemErro != null && !mensagemErro.isEmpty()) {
                notificacao.setErrorMessage(mensagemErro);
            }
            notificacaoJpaRepository.save(notificacao);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao atualizar status da notificação: " + e.getMessage(), e);
        }
    }
}

