package com.fachada.cagepa.fachadacagepa.facade.proxy;

import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePfDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ClientePjDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.EnderecoDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.HidrometroDTO;
import com.fachada.cagepa.fachadacagepa.domain.application.services.AdminService;
import com.fachada.cagepa.fachadacagepa.domain.application.services.AuthService;
import com.fachada.cagepa.fachadacagepa.domain.application.services.ClienteService;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoCliente;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.TipoEndereco;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.domain.application.services.AuditLoggerService;
import com.fachada.cagepa.fachadacagepa.facade.ISecurePainelCagepaFacadeProxy;
import com.fachada.cagepa.fachadacagepa.facade.PainelCagepaFacade;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class SecurePainelCagepaFacadeProxy implements ISecurePainelCagepaFacadeProxy {
    private final PainelCagepaFacade painelCagepaFacade;
    private final AuthService authService;
    private final AuditLoggerService auditLogger;
    private final AdminService adminService;
    private final ClienteService clienteService;

    @Autowired
    public SecurePainelCagepaFacadeProxy(PainelCagepaFacade painelCagepaFacade, AuthService authService, AuditLoggerService auditLogger, AdminService adminService, ClienteService clienteService) {
        this.painelCagepaFacade = painelCagepaFacade;
        this.authService = authService;
        this.auditLogger = auditLogger;
        this.adminService = adminService;
        this.clienteService = clienteService;
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

    @Override
    @Transactional
    public boolean desativarAdmin(String token, String username) {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "DESATIVAR_ADMIN", "Token invalido", "Usuario nao autenticado");
            return false;
        }

        try {
            boolean resultado = adminService.desativarAdminPorUsername(username);
            auditLogger.logSucesso(token, "DESATIVAR_ADMIN", "Admin '" + username + "' desativado com sucesso");
            return resultado;
        } catch (IllegalArgumentException e) {
            auditLogger.logErro(token, "DESATIVAR_ADMIN", "Erro ao desativar admin", e.getMessage());
            return false;
        }
    }

    @Override
    public List<Cliente> listarClientes(String token) {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "LISTAR_CLIENTES", "Token invalido", "Usuario nao autenticado");
            return null;
        }

        try {
            List<Cliente> clientes = clienteService.obterTodosClientes();
            auditLogger.logSucesso(token, "LISTAR_CLIENTES", "Total de clientes listados: " + clientes.size());
            return clientes;
        } catch (Exception e) {
            auditLogger.logErro(token, "LISTAR_CLIENTES", "Erro ao listar clientes", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente obterClientePorCpfCnpj(String token, String cpfCnpj) {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "OBTER_CLIENTE_COMPLETO", "Token invalido", "Usuario nao autenticado");
            return null;
        }

        try {
            var cliente = clienteService.obterClientePorCpfCnpj(cpfCnpj);
            if (cliente.isPresent()) {
                var clienteData = cliente.get();
                // Inicializa as coleções lazy-loaded
                clienteData.getEnderecos().size();
                clienteData.getHidrometros().size();
                auditLogger.logSucesso(token, "OBTER_CLIENTE_COMPLETO", "Cliente obtido: " + cpfCnpj);
                return clienteData;
            } else {
                auditLogger.logErro(token, "OBTER_CLIENTE_COMPLETO", "Cliente nao encontrado", cpfCnpj);
                return null;
            }
        } catch (Exception e) {
            auditLogger.logErro(token, "OBTER_CLIENTE_COMPLETO", "Erro ao obter cliente", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean adicionarEnderecoCliente(String token, String cpfCnpj, 
                                           String logradouro, String numero, String complemento,
                                           String bairro, String cidade, String estado, String cep) {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "ADICIONAR_ENDERECO", "Token invalido", "Usuario nao autenticado");
            return false;
        }

        try {
            var endereco = new EnderecoDTO(logradouro, numero, complemento, bairro, cidade, estado, cep, TipoEndereco.RESIDENCIAL);
            clienteService.adicionarEnderecoCliente(cpfCnpj, endereco);
            auditLogger.logSucesso(token, "ADICIONAR_ENDERECO", "Endereco adicionado ao cliente: " + cpfCnpj);
            return true;
        } catch (Exception e) {
            auditLogger.logErro(token, "ADICIONAR_ENDERECO", "Erro ao adicionar endereco", e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro> obterHidrometrosPorCliente(String token, String clienteCpfCnpj) {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "LISTAR_HIDROMETROS", "Token invalido", "Usuario nao autenticado");
            return null;
        }

        try {
            var hidrometros = painelCagepaFacade.obterHidrometrosPorCliente(clienteCpfCnpj);
            // Inicializa os endereços lazy-loaded antes de retornar
            if (hidrometros != null) {
                for (var h : hidrometros) {
                    if (h.getEnderecoInstalacao() != null) {
                        h.getEnderecoInstalacao().getLogradouro(); // Força inicialização
                    }
                }
            }
            auditLogger.logSucesso(token, "LISTAR_HIDROMETROS", "Hidrometros listados para cliente: " + clienteCpfCnpj);
            return hidrometros;
        } catch (Exception e) {
            auditLogger.logErro(token, "LISTAR_HIDROMETROS", "Erro ao listar hidrometros", e.getMessage());
            return null;
        }
    }

    @Override
    @Transactional
    public boolean alterarStatusHidrometro(String token, String shaId, boolean ativo) {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "ALTERAR_STATUS_HIDROMETRO", "Token invalido", "Usuario nao autenticado");
            return false;
        }

        try {
            var resultado = painelCagepaFacade.alterarStatusHidrometro(shaId, ativo);
            auditLogger.logSucesso(token, "ALTERAR_STATUS_HIDROMETRO", 
                "Hydrometro " + shaId + " status alterado para: " + (ativo ? "ATIVO" : "INATIVO"));
            return resultado;
        } catch (Exception e) {
            auditLogger.logErro(token, "ALTERAR_STATUS_HIDROMETRO", "Erro ao alterar status", e.getMessage());
            return false;
        }
    }

    // ==================== Métodos de RF-021 a RF-039 ====================

    /**
     * RF-021: Buscar um hidrômetro pelo identificador SHA
     */
    @Override
    @Transactional
    public com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro obterHidrometroPorSha(String token, String idSha) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "BUSCAR_HIDROMETRO_SHA", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var hidrometro = painelCagepaFacade.obterHidrometroPorSha(idSha);
            auditLogger.logSucesso(token, "BUSCAR_HIDROMETRO_SHA", "Hidrometro encontrado: " + idSha);
            return hidrometro;
        } catch (ValidationException e) {
            auditLogger.logErro(token, "BUSCAR_HIDROMETRO_SHA", "Erro na validacao", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro(token, "BUSCAR_HIDROMETRO_SHA", "Erro ao buscar hidrometro", e.getMessage());
            throw new ValidationException("Erro ao buscar hidrometro: " + e.getMessage());
        }
    }

    /**
     * RF-032: Retorna o consumo individual de cada hidrômetro do cliente
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoHidrometroDTO> obterConsumoIndividualPorHidrometro(String token, String clienteCpfCnpj) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "CONSUMO_INDIVIDUAL_HIDROMETRO", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var consumos = painelCagepaFacade.obterConsumoIndividualPorHidrometro(clienteCpfCnpj);
            auditLogger.logSucesso(token, "CONSUMO_INDIVIDUAL_HIDROMETRO", 
                "Consumo individual obtido para cliente: " + clienteCpfCnpj);
            return consumos;
        } catch (ValidationException e) {
            auditLogger.logErro(token, "CONSUMO_INDIVIDUAL_HIDROMETRO", "Erro na validacao", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro(token, "CONSUMO_INDIVIDUAL_HIDROMETRO", "Erro ao obter consumo", e.getMessage());
            throw new ValidationException("Erro ao obter consumo: " + e.getMessage());
        }
    }

    /**
     * RF-033: Retorna a soma do consumo de todos os hidrômetros do cliente
     */
    @Override
    @Transactional(readOnly = true)
    public int obterConsumoTotalCliente(String token, String clienteCpfCnpj) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "CONSUMO_TOTAL_CLIENTE", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var consumoTotal = painelCagepaFacade.obterConsumoTotalCliente(clienteCpfCnpj);
            auditLogger.logSucesso(token, "CONSUMO_TOTAL_CLIENTE", 
                "Consumo total obtido para cliente: " + clienteCpfCnpj + " = " + consumoTotal + " m3");
            return consumoTotal;
        } catch (ValidationException e) {
            auditLogger.logErro(token, "CONSUMO_TOTAL_CLIENTE", "Erro na validacao", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro(token, "CONSUMO_TOTAL_CLIENTE", "Erro ao obter consumo total", e.getMessage());
            throw new ValidationException("Erro ao obter consumo total: " + e.getMessage());
        }
    }

    /**
     * RF-036: Retorna os emails enviados para clientes que ultrapassaram >= 70% do limite
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao> obterEmailsNotificacoes(String token) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "LISTAR_EMAILS_NOTIFICACOES", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var emails = painelCagepaFacade.obterEmailsNotificacoes();
            auditLogger.logSucesso(token, "LISTAR_EMAILS_NOTIFICACOES", 
                "Emails de notificacoes listados: " + emails.size());
            return emails;
        } catch (Exception e) {
            auditLogger.logErro(token, "LISTAR_EMAILS_NOTIFICACOES", "Erro ao listar emails", e.getMessage());
            throw new ValidationException("Erro ao listar emails: " + e.getMessage());
        }
    }

    /**
     * RF-037: Retorna histórico de notificações enviadas
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao> obterHistoricoNotificacoes(String token, String clienteCpfCnpj) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "HISTORICO_NOTIFICACOES", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var historico = painelCagepaFacade.obterHistoricoNotificacoes(clienteCpfCnpj);
            auditLogger.logSucesso(token, "HISTORICO_NOTIFICACOES", 
                "Historico de notificacoes obtido para cliente: " + clienteCpfCnpj);
            return historico;
        } catch (ValidationException e) {
            auditLogger.logErro(token, "HISTORICO_NOTIFICACOES", "Erro na validacao", e.getMessage());
            throw e;
        } catch (Exception e) {
            auditLogger.logErro(token, "HISTORICO_NOTIFICACOES", "Erro ao obter historico", e.getMessage());
            throw new ValidationException("Erro ao obter historico: " + e.getMessage());
        }
    }

    /**
     * RF-038: Verifica se pode enviar notificação (não duplicada no mesmo dia)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean podeEnviarNotificacao(String token, String clienteCpfCnpj, String hidrometroIdSha) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "VALIDAR_NOTIFICACAO_DUPLICADA", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var podeEnviar = painelCagepaFacade.podeEnviarNotificacao(clienteCpfCnpj, hidrometroIdSha);
            if (podeEnviar) {
                auditLogger.logSucesso(token, "VALIDAR_NOTIFICACAO_DUPLICADA", 
                    "Notificacao pode ser enviada - Cliente: " + clienteCpfCnpj + " Hidrometro: " + hidrometroIdSha);
            } else {
                auditLogger.logAviso(token, "VALIDAR_NOTIFICACAO_DUPLICADA", 
                    "Notificacao duplicada detectada no mesmo dia - Cliente: " + clienteCpfCnpj + " Hidrometro: " + hidrometroIdSha);
            }
            return podeEnviar;
        } catch (Exception e) {
            auditLogger.logErro(token, "VALIDAR_NOTIFICACAO_DUPLICADA", "Erro ao validar", e.getMessage());
            throw new ValidationException("Erro ao validar notificacao: " + e.getMessage());
        }
    }

    /**
     * RF-039: Retorna histórico de auditoria das operações realizadas no sistema
     * Permite rastreabilidade das operações: CRUD, Usuário, Timestamp, Resultado
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.List<com.fachada.cagepa.fachadacagepa.infra.persistence.AuditEntry> obterHistoricoAuditoriaCompleto(String token) throws ValidationException {
        if (!authService.isAuthenticated(token)) {
            auditLogger.logErro(token, "HISTORICO_AUDITORIA_COMPLETO", "Token invalido", "Usuario nao autenticado");
            throw new ValidationException("Usuario nao autenticado");
        }

        try {
            var auditoria = painelCagepaFacade.obterHistoricoAuditoria();
            auditLogger.logSucesso(token, "HISTORICO_AUDITORIA_COMPLETO", 
                "Historico de auditoria obtido: " + auditoria.size() + " registros");
            return auditoria;
        } catch (Exception e) {
            auditLogger.logErro(token, "HISTORICO_AUDITORIA_COMPLETO", "Erro ao obter historico", e.getMessage());
            throw new ValidationException("Erro ao obter historico de auditoria: " + e.getMessage());
        }
    }
}
