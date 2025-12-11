package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.ConsumoLimiarStrategy;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoConsumo;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoObserver;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoStrategy;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IHidrometroJpaRepository;

import jakarta.transaction.Transactional;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoConsumoService {

    private static final Logger logger = LoggerFactory.getLogger("NOTIFICACAO");
    private static final double LIMIAR_PADRAO = 70.0;

    private final IHidrometroJpaRepository hidrometroRepository;

    private final ConsumoService consumoService;

    private final NotificacaoStrategy notificacaoStrategy;

    @Getter
    private final NotificacaoObserver notificacaoObserver = new NotificacaoObserver();
    @Getter
    private final ConsumoLimiarStrategy consumoLimiarStrategy = new ConsumoLimiarStrategy(LIMIAR_PADRAO);

    public NotificacaoConsumoService(IHidrometroJpaRepository hidrometroRepository, ConsumoService consumoService, NotificacaoStrategy notificacaoStrategy) {
        this.hidrometroRepository = hidrometroRepository;
        this.consumoService = consumoService;
        this.notificacaoStrategy = notificacaoStrategy;
    }

    @Transactional
    public void verificarENotificarConsumoAlto() {
        logger.info("Iniciando verificacao de consumo de hidrometros...");

        List<Hidrometro> hidrometros = hidrometroRepository.findAll();
        int notificacoesEnviadas = 0;
        int erros = 0;

        for (Hidrometro hidrometro : hidrometros) {
            try {
                if (hidrometro.getLimiteConsumoMensalM3() == null) {
                    logger.warn("Hidrometro {} nao tem limite de consumo definido", hidrometro.getIdSha());
                    continue;
                }

                Cliente cliente = hidrometro.getCliente();
                if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
                    logger.warn("Hidrometro {} nao tem cliente ou email associado", hidrometro.getIdSha());
                    continue;
                }

                Double consumoMensal = consumoService.calcularConsumoMensalHidrometro(
                    hidrometro.getIdSha(),
                    LocalDate.now().getYear(),
                    LocalDate.now().getMonthValue()
                );

                if (consumoMensal == null) {
                    consumoMensal = 0.0;
                }

                double percentualConsumo = (consumoMensal / hidrometro.getLimiteConsumoMensalM3()) * 100;

                if (consumoLimiarStrategy.deveCautivar(percentualConsumo)) {
                    NotificacaoConsumo notificacao = new NotificacaoConsumo(
                        cliente.getNomeCompleto(),
                        cliente.getEmail(),
                        hidrometro.getIdSha(),
                        consumoMensal,
                        hidrometro.getLimiteConsumoMensalM3(),
                        percentualConsumo
                    );

                    if (notificacaoStrategy != null && notificacaoStrategy.enviarNotificacao(notificacao)) {
                        notificacoesEnviadas++;
                        notificacaoObserver.registrarNotificacao(notificacao);
                        logger.info("Notificacao enviada para cliente {} | Hidrometro: {} | Consumo: {} m3 ({}%)", cliente.getNomeCompleto(), hidrometro.getIdSha(), String.format("%.2f", consumoMensal), String.format("%.1f", percentualConsumo));
                    } else {
                        erros++;
                        logger.error("Falha ao enviar notificacao para cliente {} | Hidrometro: {}", cliente.getNomeCompleto(), hidrometro.getIdSha());
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao processar notificacao para hidrometro: {} | Erro: {}", hidrometro.getIdSha(), e.getMessage(), e);
                erros++;
            }
        }

        logger.info("Verificacao concluida. Notificacoes enviadas: {} | Erros: {}", notificacoesEnviadas, erros);
    }

    public List<NotificacaoConsumo> obterRelatorioNotificacoes() {
        List<NotificacaoConsumo> notificacoes = notificacaoObserver.obterNotificacoes();

        if (notificacoes.isEmpty()) {
            return null;
        }

        return notificacoes;
    }

    public void setNovoLimiar(double novoLimiar) {
        consumoLimiarStrategy.setLimiarPercentual(novoLimiar);
        logger.info("Novo limiar de notificacao definido: {}%", novoLimiar);
    }
}

