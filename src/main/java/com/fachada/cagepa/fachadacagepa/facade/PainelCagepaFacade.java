package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.config.SystemConfiguration;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.services.*;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ImageProcessorFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.LeituraHidrometroFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.FachadaImageObserver;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.ImageWatcher;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.command.CommandInvoker;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Component
public class PainelCagepaFacade {

    @Getter
    private final CommandInvoker commandInvoker = new CommandInvoker();

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ConsumoService consumoService;

    private ImageWatcher imageWatcher;

    @Autowired
    private HidrometroService hidrometroService;

    @Autowired
    private ImageProcessorFactory imageProcessorFactory;

    @Autowired
    private com.fachada.cagepa.fachadacagepa.domain.enterprise.audit.AuditLoggerService auditLogger;

    @Autowired
    private com.fachada.cagepa.fachadacagepa.domain.application.services.NotificacaoConsumoService notificacaoConsumoService;

    private ConfigurationFacade configurationFacade;

    public PainelCagepaFacade(ImageProcessorFactory processorFactory, FachadaImageObserver observer) {
        this.imageProcessorFactory = processorFactory;
        try {
            String dir = SystemConfiguration.getInstance().getImageDirectory();
            this.imageWatcher = new ImageWatcher(dir);
            this.imageWatcher.addObserver(observer);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar ImageWatcher", e);
        }
    }

    public void iniciarMonitoramento() throws IOException {
        imageWatcher.startWatching();
        System.out.println("Monitoramento iniciado em: " + SystemConfiguration.getInstance().getImageDirectory());
    }

    public void pararMonitoramento() {
        if (imageWatcher != null) {
            imageWatcher.stopWatching();
        }
    }

    public boolean criarAdmin(String username, String password) throws ValidationException {
        var newAdmin = adminService.createAdmin(username, password);

        return newAdmin != null;
    }

    @Transactional
    public void criarClientePf(ClientePfDTO clientePfDTO) throws ValidationException {
        clienteService.criarClientePessoaFisica(clientePfDTO);
    }

    @Transactional
    public void criarClientePj(ClientePjDTO clientePjDTO) throws ValidationException {
        clienteService.criarClientePessoaJuridica(clientePjDTO);
    }

    @Transactional
    public void criarHidrometro(HidrometroDTO hidrometroDTO) throws ValidationException {
        hidrometroService.salvarHidrometro(hidrometroDTO);
    }

    // ==================== Métodos de Consumo ====================

    /**
     * Obtém consumo diário de um cliente (data atual)
     */
    public ConsumoClientePeriodoDTO obterConsumoDiario(String clienteCpfCnpj) throws ValidationException {
        return consumoService.calcularConsumoDiario(clienteCpfCnpj);
    }

    /**
     * Obtém consumo semanal de um cliente (semana atual)
     */
    public ConsumoClientePeriodoDTO obterConsumoSemanal(String clienteCpfCnpj) throws ValidationException {
        return consumoService.calcularConsumoSemanal(clienteCpfCnpj);
    }

    /**
     * Obtém consumo mensal de um cliente (mês atual)
     */
    public ConsumoClientePeriodoDTO obterConsumoMensal(String clienteCpfCnpj) throws ValidationException {
        return consumoService.calcularConsumoMensal(clienteCpfCnpj);
    }

    /**
     * Obtém consumo anual de um cliente (ano atual)
     */
    public ConsumoClientePeriodoDTO obterConsumoAnual(String clienteCpfCnpj) throws ValidationException {
        return consumoService.calcularConsumoAnual(clienteCpfCnpj);
    }

    public CommandInvoker getCommandInvoker() {
        return commandInvoker;
    }

    public ConfigurationFacade getConfigurationFacade() {
        if (configurationFacade == null) {
            try {
                configurationFacade = new ConfigurationFacade(SystemConfiguration.getInstance());
            } catch (Exception e) {
                throw new RuntimeException("Erro ao inicializar ConfigurationFacade", e);
            }
        }
        return configurationFacade;
    }

    // ==================== Metodos de Notificacao ====================

    /**
     * Verifica hidrometros com consumo alto e envia notificacoes automaticamente
     */
    public void verificarENotificarConsumoAlto() {
        notificacaoConsumoService.verificarENotificarConsumoAlto();
    }

    /**
     * Retorna lista de clientes que foram notificados
     */
    public String obterRelatorioNotificacoes() {
        var observer = notificacaoConsumoService.getNotificacaoObserver();
        var notificacoes = observer.obterNotificacoes();

        StringBuilder relatorio = new StringBuilder();
        relatorio.append("\n========== RELATORIO DE NOTIFICACOES ==========\n");
        relatorio.append("Total de Notificacoes: ").append(observer.getTotalNotificacoes()).append("\n");
        relatorio.append("Enviadas: ").append(observer.getTotalNotificacoesEnviadas()).append("\n");
        relatorio.append("Com Erro: ").append(observer.getTotalNotificacoesComErro()).append("\n");
        relatorio.append("\n---------- Detalhes ----------\n");

        if (notificacoes.isEmpty()) {
            relatorio.append("Nenhuma notificacao registrada.\n");
        } else {
            for (var notificacao : notificacoes) {
                relatorio.append("\nCliente: ").append(notificacao.getClienteNome()).append("\n");
                relatorio.append("Email: ").append(notificacao.getClienteEmail()).append("\n");
                relatorio.append("Hidrometro: ").append(notificacao.getHidrometroId()).append("\n");
                relatorio.append("Consumo Atual: ").append(String.format("%.2f", notificacao.getConsumoAtual())).append(" m3\n");
                relatorio.append("Limite: ").append(notificacao.getLimiteConsumo()).append(" m3\n");
                relatorio.append("Percentual: ").append(String.format("%.2f", notificacao.getPercentualConsumo())).append("%\n");
                relatorio.append("Status: ").append(notificacao.getStatus()).append("\n");
                relatorio.append("Data: ").append(notificacao.getDataNotificacao()).append("\n");
                relatorio.append("---\n");
            }
        }

        relatorio.append("\n==========================================\n");
        return relatorio.toString();
    }

    /**
     * Altera o limiar de notificacao (padrao: 70%)
     */
    public void alterarLimiarNotificacao(double novoLimiar) {
        notificacaoConsumoService.setNovoLimiar(novoLimiar);
    }
}
