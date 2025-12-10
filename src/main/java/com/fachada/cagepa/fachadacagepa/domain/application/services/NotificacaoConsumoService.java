package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.ConsumoLimiarStrategy;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoConsumo;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoObserver;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.notification.NotificacaoStrategy;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IHidrometroJpaRepository;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacaoConsumoService {

    private static final Logger logger = LoggerFactory.getLogger("NOTIFICACAO");
    private static final double LIMIAR_PADRAO = 70.0;

    @Autowired
    private IHidrometroJpaRepository hidrometroRepository;

    @Autowired
    private ConsumoService consumoService;

    @Autowired
    private NotificacaoStrategy notificacaoStrategy;

    private final NotificacaoObserver notificacaoObserver = new NotificacaoObserver();
    private final ConsumoLimiarStrategy consumoLimiarStrategy = new ConsumoLimiarStrategy(LIMIAR_PADRAO);

    @Transactional
    public void verificarENotificarConsumoAlto() {
        logger.info("Iniciando verificacao de consumo de hidrometros...");

        List<Hidrometro> hidrometros = hidrometroRepository.findAll();
        int notificacoesEnviadas = 0;
        int erros = 0;

        for (Hidrometro hidrometro : hidrometros) {
            try {
                if (hidrometro.getLimiteConsumoMensalM3() == null) {
                    logger.warn("Hidrometro " + hidrometro.getIdSha() + " nao tem limite de consumo definido");
                    continue;
                }

                Cliente cliente = hidrometro.getCliente();
                if (cliente == null || cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
                    logger.warn("Hidrometro " + hidrometro.getIdSha() + " nao tem cliente ou email associado");
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
                        logger.info("Notificacao enviada para cliente " + cliente.getNomeCompleto() +
                                " | Hidrometro: " + hidrometro.getIdSha() +
                                " | Consumo: " + String.format("%.2f", consumoMensal) + " m3 (" +
                                String.format("%.1f", percentualConsumo) + "%)");
                    } else {
                        erros++;
                        logger.error("Falha ao enviar notificacao para cliente " + cliente.getNomeCompleto() +
                                " | Hidrometro: " + hidrometro.getIdSha());
                    }
                }
            } catch (Exception e) {
                logger.error("Erro ao processar notificacao para hidrometro: " +
                    hidrometro.getIdSha() + " | Erro: " + e.getMessage(), e);
                erros++;
            }
        }

        logger.info("Verificacao concluida. Notificacoes enviadas: " + notificacoesEnviadas +
                   " | Erros: " + erros);
    }

    public NotificacaoObserver getNotificacaoObserver() {
        return notificacaoObserver;
    }

    public String obterRelatorioNotificacoes() {
        List<NotificacaoConsumo> notificacoes = notificacaoObserver.obterNotificacoes();

        if (notificacoes.isEmpty()) {
            return "Nenhuma notificacao foi enviada ainda.";
        }

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("\n========== RELATORIO DE NOTIFICACOES ==========\n");
        relatorio.append("Total de Notificacoes: ").append(notificacaoObserver.getTotalNotificacoes()).append("\n");
        relatorio.append("Notificacoes Enviadas: ").append(notificacaoObserver.getTotalNotificacoesEnviadas()).append("\n");
        relatorio.append("\n========== DETALHES ==========\n");

        for (NotificacaoConsumo notificacao : notificacoes) {
            relatorio.append("\nCliente: ").append(notificacao.getClienteNome()).append("\n");
            relatorio.append("Email: ").append(notificacao.getClienteEmail()).append("\n");
            relatorio.append("Hidrometro: ").append(notificacao.getHidrometroId()).append("\n");
            relatorio.append("Consumo Atual: ").append(String.format("%.2f", notificacao.getConsumoAtual()))
                    .append(" m3\n");
            relatorio.append("Limite Mensal: ").append(notificacao.getLimiteConsumo()).append(" m3\n");
            relatorio.append("Percentual: ").append(String.format("%.1f", notificacao.getPercentualConsumo()))
                    .append("%\n");
            relatorio.append("Status: ").append(notificacao.getStatus()).append("\n");
            relatorio.append("Data: ").append(notificacao.getDataNotificacao()).append("\n");
            relatorio.append("---\n");
        }

        return relatorio.toString();
    }

    public ConsumoLimiarStrategy getConsumoLimiarStrategy() {
        return consumoLimiarStrategy;
    }

    public void setNovoLimiar(double novoLimiar) {
        consumoLimiarStrategy.setLimiarPercentual(novoLimiar);
        logger.info("Novo limiar de notificacao definido: " + novoLimiar + "%");
    }
}

