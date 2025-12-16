package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.config.SystemConfiguration;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoHidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.services.*;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ImageProcessorFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.FachadaImageObserver;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.ImageWatcher;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.command.CommandInvoker;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

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
    private AuditLoggerService auditLogger;

    @Autowired
    private com.fachada.cagepa.fachadacagepa.domain.application.services.NotificacaoConsumoService notificacaoConsumoService;

    @Autowired
    private NotificacaoPersistenciaService notificacaoPersistenciaService;

    @Autowired
    private AuditService auditService;

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

    /**
     * RF-032: Retorna o consumo individual de cada hidrômetro do cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Lista com consumo individual de cada hidrometro
     */
    @Transactional(readOnly = true)
    public List<ConsumoHidrometroDTO> obterConsumoIndividualPorHidrometro(String clienteCpfCnpj) throws ValidationException {
        return consumoService.obterConsumoIndividualPorHidrometro(clienteCpfCnpj);
    }

    /**
     * RF-033: Retorna a soma do consumo de todos os hidrômetros do cliente
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Consumo total de todos os hidrometros
     */
    @Transactional(readOnly = true)
    public int obterConsumoTotalCliente(String clienteCpfCnpj) throws ValidationException {
        return consumoService.obterConsumoTotalCliente(clienteCpfCnpj);
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

    /**
     * Retorna os emails enviados para clientes que ultrapassaram >= 70% do limite
     * @return Lista de notificações enviadas
     */
    @Transactional
    public List<Notificacao> obterEmailsNotificacoes() {
        try {
            return notificacaoPersistenciaService.obterHistoricoNotificacoesPorPeriodo(
                    java.time.LocalDateTime.now().minusDays(30),
                    java.time.LocalDateTime.now()
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter emails de notificações: " + e.getMessage(), e);
        }
    }

    /**
     * Retorna histórico de notificações enviadas
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @return Lista de notificações do cliente
     */
    @Transactional
    public List<Notificacao> obterHistoricoNotificacoes(String clienteCpfCnpj) throws ValidationException {
        try {
            return notificacaoPersistenciaService.obterHistoricoNotificacoesCliente(clienteCpfCnpj);
        } catch (Exception e) {
            throw new ValidationException("Erro ao obter histórico de notificações: " + e.getMessage());
        }
    }

    /**
     * Verifica se pode enviar notificação (não duplicada no mesmo dia)
     * @param clienteCpfCnpj CPF/CNPJ do cliente
     * @param hidrometroIdSha ID SHA do hidrometro
     * @return true se pode enviar, false se já foi enviada hoje
     */
    @Transactional
    public boolean podeEnviarNotificacao(String clienteCpfCnpj, String hidrometroIdSha) {
        return notificacaoPersistenciaService.podeEnviarNotificacao(clienteCpfCnpj, hidrometroIdSha);
    }

    /**
     * Busca um hidrômetro pelo identificador SHA
     * @param idSha ID SHA do hidrômetro
     * @return Dados do hidrômetro
     */
    @Transactional
    public Hidrometro obterHidrometroPorSha(String idSha) throws ValidationException {
        try {
            return hidrometroService.obterHidrometroPorSha(idSha)
                    .orElseThrow(() -> new ValidationException("Hidrometro não encontrado com ID SHA: " + idSha));
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("Erro ao buscar hidrometro: " + e.getMessage());
        }
    }

    @Transactional
    public java.util.List<Hidrometro> obterHidrometrosPorCliente(String clienteCpfCnpj) {
        return hidrometroService.obterHidrometrosPorCliente(clienteCpfCnpj);
    }

    @Transactional
    public boolean alterarStatusHidrometro(String idSha, boolean ativo) {
        return hidrometroService.alterarStatusHidrometro(idSha, ativo);
    }

    /**
     * Retorna histórico de auditoria das operações realizadas no sistema
     * Permite rastreabilidade das operações: CRUD, Usuário, Timestamp, Resultado
     * @return Lista de entradas de auditoria
     */
    @Transactional
    public List<com.fachada.cagepa.fachadacagepa.infra.persistence.AuditEntry> obterHistoricoAuditoria() {
        return auditService.obterHistoricoAuditoria();
    }
}
