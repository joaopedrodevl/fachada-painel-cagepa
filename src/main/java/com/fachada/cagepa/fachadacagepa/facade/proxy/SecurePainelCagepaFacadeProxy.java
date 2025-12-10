package com.fachada.cagepa.fachadacagepa.facade.proxy;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.services.AuthService;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.audit.AuditLoggerService;
import com.fachada.cagepa.fachadacagepa.facade.ISecurePainelCagepaFacadeProxy;
import com.fachada.cagepa.fachadacagepa.facade.PainelCagepaFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;

@Component
public class SecurePainelCagepaFacadeProxy implements ISecurePainelCagepaFacadeProxy {
    private final PainelCagepaFacade painelCagepaFacade;
    private final AuthService authService;
    private final AuditLoggerService auditLogger;

    @Autowired
    public SecurePainelCagepaFacadeProxy(PainelCagepaFacade painelCagepaFacade, AuthService authService, AuditLoggerService auditLogger) {
        this.painelCagepaFacade = painelCagepaFacade;
        this.authService = authService;
        this.auditLogger = auditLogger;
        this.authService.initializeDefaultAdmin();
    }

    @Override
    public String login(String username, String password) throws IOException {
        var login = authService.login(username, password);

        if (login != null) {
            painelCagepaFacade.iniciarMonitoramento();
            auditLogger.logAcesso(username, "LOGIN_SUCESSO");
        } else {
            auditLogger.logAviso(username, "LOGIN_FALHOU", "Credenciais inválidas");
        }

        return login;
    }

    @Override
    @Transactional
    public boolean criarAdmin(String token, String username, String password) {
        try {
            if (authService.isAuthenticated(token)) {
                return painelCagepaFacade.criarAdmin(username, password);
            }
            return false;
        } catch (ValidationException e) {
            auditLogger.logErro(token, "CRIAR_ADMIN_FALHOU", "Erro ao criar admin", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional
    public ClientePfDTO criarClientePf(String token, String cpf, String nome, String email, String telefone, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep, String tipoEndereco, String tipoCliente) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            var cliente = new ClientePfDTO(
                    cpf,
                    nome,
                    email,
                    telefone,
                    new EnderecoDTO(
                            logradouro,
                            numero,
                            complemento,
                            bairro,
                            cidade,
                            estado,
                            cep,
                            TipoEndereco.valueOf(tipoEndereco)
                    ),
                    TipoCliente.valueOf(tipoCliente)
            );
            painelCagepaFacade.criarClientePf(cliente);

            return cliente;
        }

        return null;
    }

    @Override
    @Transactional
    public ClientePjDTO criarClientePj(String token, String cnpj, String nomeFantasia, String razaoSocial, String email, String telefone, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep, String tipoEndereco, String tipoCliente) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            var cliente = new ClientePjDTO(
                    cnpj,
                    nomeFantasia,
                    razaoSocial,
                    email,
                    telefone,
                    new EnderecoDTO(
                            logradouro,
                            numero,
                            complemento,
                            bairro,
                            cidade,
                            estado,
                            cep,
                            TipoEndereco.valueOf(tipoEndereco)
                    ),
                    TipoCliente.valueOf(tipoCliente)
            );
            painelCagepaFacade.criarClientePj(cliente);

            return cliente;
        }

        return null;
    }


    @Override
    @Transactional
    public HidrometroDTO registrarHidrometro(String token, String idSha, LocalDate dataInstalacao, String status, Integer limiteConsumoMensalM3, String clienteCpfCnpj, String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep, String tipoEndereco) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            var hidrometro = new HidrometroDTO(
                    idSha,
                    dataInstalacao,
                    status,
                    limiteConsumoMensalM3,
                    clienteCpfCnpj,
                    new EnderecoDTO(
                            logradouro,
                            numero,
                            complemento,
                            bairro,
                            cidade,
                            estado,
                            cep,
                            TipoEndereco.valueOf(tipoEndereco)
                    )
            );
            painelCagepaFacade.criarHidrometro(hidrometro);
            return hidrometro;
        }

        return null;
    }

    // ==================== Métodos de Consumo ====================

    /**
     * Obtém consumo diário de um cliente (data atual)
     */
    @Override
    public ConsumoClientePeriodoDTO obterConsumoDiario(String token, String clienteCpfCnpj) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            return painelCagepaFacade.obterConsumoDiario(clienteCpfCnpj);
        }
        return null;
    }

    /**
     * Obtém consumo semanal de um cliente (semana atual)
     */
    @Override
    public ConsumoClientePeriodoDTO obterConsumoSemanal(String token, String clienteCpfCnpj) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            return painelCagepaFacade.obterConsumoSemanal(clienteCpfCnpj);
        }
        return null;
    }

    /**
     * Obtém consumo mensal de um cliente (mês atual)
     */
    @Override
    public ConsumoClientePeriodoDTO obterConsumoMensal(String token, String clienteCpfCnpj) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            return painelCagepaFacade.obterConsumoMensal(clienteCpfCnpj);
        }
        return null;
    }

    /**
     * Obtém consumo anual de um cliente (ano atual)
     */
    @Override
    public ConsumoClientePeriodoDTO obterConsumoAnual(String token, String clienteCpfCnpj) throws ValidationException {
        if (authService.isAuthenticated(token)) {
            return painelCagepaFacade.obterConsumoAnual(clienteCpfCnpj);
        }
        return null;
    }

    /**
     * Obtém o histórico de auditoria de operacoes executadas
     */
    public String obterHistoricoAuditoria(String token) {
        if (authService.isAuthenticated(token)) {
            var history = painelCagepaFacade.getCommandInvoker().getHistory();
            if (history.isEmpty()) {
                return "Nenhuma operacao registrada no historico";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Historico de Auditoria:\n");
            int count = 1;
            for (var command : history) {
                sb.append(count).append(". ").append(command.getDescription())
                  .append(" (Usuario: ").append(command.getExecutor()).append(")\n");
                count++;
            }
            return sb.toString();
        }
        return "Nao autenticado";
    }

    /**
     * Limpa o histórico de auditoria
     */
    public boolean limparHistoricoAuditoria(String token) {
        if (authService.isAuthenticated(token)) {
            painelCagepaFacade.getCommandInvoker().clearHistory();
            return true;
        }
        return false;
    }

    // ==================== Metodos de Configuracao ====================

    /**
     * Exibe a configuracao atual do sistema
     */
    public String exibirConfiguracaoAtual(String token) {
        if (authService.isAuthenticated(token)) {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("\n========== CONFIGURACAO ATUAL ==========\n");
                sb.append("Diretorio de imagens: ").append(
                    painelCagepaFacade.getConfigurationFacade().obterDiretorioImagens()).append("\n");
                sb.append("Caminho Tesseract: ").append(
                    painelCagepaFacade.getConfigurationFacade().obterCaminhoTesseract()).append("\n");
                sb.append("Sistema Operacional: ").append(
                    painelCagepaFacade.getConfigurationFacade().obterSistemaOperacional()).append("\n");
                sb.append("========================================\n");
                return sb.toString();
            } catch (Exception e) {
                return "Erro ao obter configuracao: " + e.getMessage();
            }
        }
        return "Nao autenticado";
    }

    /**
     * Configura o diretorio de imagens
     */
    public boolean configurarDiretorioImagens(String token, String novoDir) {
        if (authService.isAuthenticated(token)) {
            try {
                painelCagepaFacade.getConfigurationFacade().configurarDiretorioImagens(novoDir);
                auditLogger.logSucesso(token, "CONFIGURAR_DIRETORIO",
                    "Diretorio de imagens alterado para: " + novoDir);
                return true;
            } catch (Exception e) {
                auditLogger.logErro(token, "CONFIGURAR_DIRETORIO",
                    "Erro ao configurar diretorio", e.getMessage());
                System.err.println("Erro: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Configura o caminho do Tesseract
     */
    public boolean configurarCaminhoTesseract(String token, String novoPath) {
        if (authService.isAuthenticated(token)) {
            try {
                painelCagepaFacade.getConfigurationFacade().configurarCaminhoTesseract(novoPath);
                auditLogger.logSucesso(token, "CONFIGURAR_TESSERACT",
                    "Caminho Tesseract alterado para: " + novoPath);
                return true;
            } catch (Exception e) {
                auditLogger.logErro(token, "CONFIGURAR_TESSERACT",
                    "Erro ao configurar Tesseract", e.getMessage());
                System.err.println("Erro: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    // ==================== Metodos de Notificacao ====================

    /**
     * Verifica e notifica clientes com consumo alto
     */
    public void verificarENotificarConsumoAlto(String token) {
        if (authService.isAuthenticated(token)) {
            painelCagepaFacade.verificarENotificarConsumoAlto();
            auditLogger.logSucesso(token, "VERIFICAR_NOTIFICACOES",
                "Verificacao de consumo realizada");
        }
    }

    /**
     * Obtém relatorio de notificacoes enviadas
     */
    public String obterRelatorioNotificacoes(String token) {
        if (authService.isAuthenticated(token)) {
            return painelCagepaFacade.obterRelatorioNotificacoes();
        }
        return "Nao autenticado";
    }

    /**
     * Altera o limiar de notificacao
     */
    public boolean alterarLimiarNotificacao(String token, double novoLimiar) {
        if (authService.isAuthenticated(token)) {
            try {
                painelCagepaFacade.alterarLimiarNotificacao(novoLimiar);
                auditLogger.logSucesso(token, "ALTERAR_LIMIAR_NOTIFICACAO",
                    "Limiar alterado para: " + novoLimiar + "%");
                return true;
            } catch (Exception e) {
                auditLogger.logErro(token, "ALTERAR_LIMIAR_NOTIFICACAO",
                    "Erro ao alterar limiar", e.getMessage());
                return false;
            }
        }
        return false;
    }
}
