package com.fachada.cagepa.fachadacagepa.infra.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para a entidade Notificacao
 */
@Repository
public interface INotificacaoJpaRepository extends JpaRepository<Notificacao, UUID> {

    /**
     * Encontra notificações por CPF/CNPJ do cliente ordenada por data descrescente
     */
    List<Notificacao> findByClienteCpfCnpjOrderByDataEnvioDesc(String clienteCpfCnpj);

    /**
     * Encontra notificações por período
     */
    List<Notificacao> findByDataEnvioBetweenOrderByDataEnvioDesc(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Encontra notificações por ID SHA do hidrometro ordenadas por data descrescente
     */
    List<Notificacao> findByHidrometroIdShaOrderByDataEnvioDesc(String hidrometroIdSha);

    /**
     * Encontra notificações para um cliente e hidrometro em um período
     * Usado para detectar duplicatas no mesmo dia
     */
    List<Notificacao> findByClienteCpfCnpjAndHidrometroIdShaAndDataEnvioBetween(
            String clienteCpfCnpj,
            String hidrometroIdSha,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    );

    /**
     * Encontra a última notificação para um cliente e hidrometro
     */
    Optional<Notificacao> findFirstByClienteCpfCnpjAndHidrometroIdShaOrderByDataEnvioDesc(
            String clienteCpfCnpj,
            String hidrometroIdSha
    );

    /**
     * Conta notificações por status de envio
     */
    long countByStatusEnvio(String statusEnvio);

    /**
     * Encontra notificações com falha
     */
    List<Notificacao> findByStatusEnvio(String statusEnvio);
}

