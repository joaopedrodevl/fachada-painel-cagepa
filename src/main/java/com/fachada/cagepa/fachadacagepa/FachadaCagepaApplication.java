package com.fachada.cagepa.fachadacagepa;

import com.fachada.cagepa.fachadacagepa.facade.proxy.SecurePainelCagepaFacadeProxy;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Hidrometro;
import com.fachada.cagepa.fachadacagepa.infra.persistence.Notificacao;
import com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoHidrometroDTO;
import com.fachada.cagepa.fachadacagepa.infra.persistence.AuditEntry;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class FachadaCagepaApplication implements CommandLineRunner {

    private final SecurePainelCagepaFacadeProxy securePainelCagepaFacadeProxy;
    private static ApplicationContext applicationContext;

    @Value("${app.cli.enabled:true}")
    private boolean cliEnabled;

    private Scanner scanner;
    private String token;

    // Valores padrão para inputs
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "Admin123@";
    private static final String DEFAULT_CPF = "949.545.430-10";
    private static final String DEFAULT_NOME_PF = "João da Silva";
    private static final String DEFAULT_EMAIL = "joao@email.com";
    private static final String DEFAULT_TELEFONE = "1199999-9999";
    private static final String DEFAULT_LOGRADOURO = "Rua A";
    private static final String DEFAULT_NUMERO = "100";
    private static final String DEFAULT_COMPLEMENTO = "";
    private static final String DEFAULT_BAIRRO = "Bairro B";
    private static final String DEFAULT_CIDADE = "Cidade C";
    private static final String DEFAULT_ESTADO = "PE";
    private static final String DEFAULT_CEP = "12345-678";

    public FachadaCagepaApplication(SecurePainelCagepaFacadeProxy securePainelCagepaFacadeProxy) {
        this.securePainelCagepaFacadeProxy = securePainelCagepaFacadeProxy;
    }

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(FachadaCagepaApplication.class, args);
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (!cliEnabled) {
            return;
        }

        scanner = new Scanner(System.in);

        System.out.println("========== CLIENTE CLI - PAINEL CAGEPA ==========\n");
        
        String username = lerString("Username: ", DEFAULT_USERNAME);
        String password = lerString("Password: ", DEFAULT_PASSWORD);
        
        System.out.println("\nRealizando login...");
        token = securePainelCagepaFacadeProxy.login(username, password);

        if (token == null) {
            System.err.println("Falha ao fazer login com as credenciais fornecidas");
            return;
        }

        System.out.println("Login realizado com sucesso!\n");

        boolean continuar = true;
        while (continuar) {
            exibirMenu();
            int opcao = lerInteiro("Escolha uma opcao: ");

            try {
                switch (opcao) {
                    case 1:
                        criarClientePF();
                        break;
                    case 2:
                        criarClientePJ();
                        break;
                    case 3:
                        registrarHidrometro();
                        break;
                    case 4:
                        obterConsumoCliente();
                        break;
                    case 5:
                        criarNovoAdmin();
                        break;
                    case 6:
                        desativarAdmin();
                        break;
                    case 7:
                        listarClientes();
                        break;
                    case 8:
                        verHistoricoAuditoria();
                        break;
                    case 9:
                        configurarSistema();
                        break;
                    case 10:
                        securePainelCagepaFacadeProxy.verificarENotificarConsumoAlto(token);
                        System.out.println("Verificacao de consumo concluida!");
                        break;
                    case 11:
                        verRelatorioNotificacoes();
                        break;
                    case 12:
                        obterClienteCompleto();
                        break;
                    case 13:
                        adicionarEnderecoCliente();
                        break;
                    case 14:
                        listarHidrometrosPorCliente();
                        break;
                    case 15:
                        alterarStatusHidrometro();
                        break;
                    case 16:
                        buscarHidrometroPorSha();
                        break;
                    case 17:
                        obterConsumoIndividualPorHidrometro();
                        break;
                    case 18:
                        obterConsumoTotalCliente();
                        break;
                    case 19:
                        listarEmailsNotificacoes();
                        break;
                    case 20:
                        obterHistoricoNotificacoes();
                        break;
                    case 21:
                        validarNotificacaoDuplicada();
                        break;
                    case 22:
                        exibirAuditoriaCompleta();
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("\nFinalizando aplicacao...");
                        encerrarAplicacao();
                        break;
                    default:
                        System.out.println("Opcao invalida!");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }

            if (continuar) {
                System.out.println("\nPressione Enter para continuar...");
                scanner.nextLine();
            }
        }
    }

    private void exibirMenu() {
        System.out.println("\n========================================");
        System.out.println("           MENU PRINCIPAL");
        System.out.println("========================================");
        System.out.println("1 - Criar Cliente (Pessoa Fisica)");
        System.out.println("2 - Criar Cliente (Pessoa Juridica)");
        System.out.println("3 - Registrar Hidrometro");
        System.out.println("4 - Obter Consumo de Cliente");
        System.out.println("5 - Criar novo Admin");
        System.out.println("6 - Desativar Admin");
        System.out.println("7 - Listar Clientes");
        System.out.println("8 - Ver Historico de Auditoria");
        System.out.println("9 - Configuracoes do Sistema");
        System.out.println("10 - Verificar e Notificar Consumo Alto");
        System.out.println("11 - Ver Relatorio de Notificacoes");
        System.out.println("12 - Obter Dados Completos de Cliente");
        System.out.println("13 - Adicionar Endereco a Cliente");
        System.out.println("14 - Listar Hidrometros por Cliente");
        System.out.println("15 - Ativar/Desativar Hidrometro");
        System.out.println("16 - Buscar Hidrometro por SHA");
        System.out.println("17 - Consumo Individual por Hidrometro");
        System.out.println("18 - Consumo Total do Cliente");
        System.out.println("19 - Listar Emails de Notificacoes");
        System.out.println("20 - Historico de Notificacoes");
        System.out.println("21 - Validar Notificacao Duplicada");
        System.out.println("22 - Auditoria Completa");
        System.out.println("0 - Sair");
        System.out.println("========================================");
    }

    private void criarClientePF() {
        System.out.println("\n========== CRIAR CLIENTE (PESSOA FÍSICA) ==========");

        String cpf = lerString("CPF (" + DEFAULT_CPF + "): ", DEFAULT_CPF);
        String nome = lerString("Nome (" + DEFAULT_NOME_PF + "): ", DEFAULT_NOME_PF);
        String email = lerString("Email (" + DEFAULT_EMAIL + "): ", DEFAULT_EMAIL);
        String telefone = lerString("Telefone (" + DEFAULT_TELEFONE + "): ", DEFAULT_TELEFONE);
        String logradouro = lerString("Logradouro (" + DEFAULT_LOGRADOURO + "): ", DEFAULT_LOGRADOURO);
        String numero = lerString("Numero (" + DEFAULT_NUMERO + "): ", DEFAULT_NUMERO);
        String complemento = lerString("Complemento (" + DEFAULT_COMPLEMENTO + "): ", DEFAULT_COMPLEMENTO);
        String bairro = lerString("Bairro (" + DEFAULT_BAIRRO + "): ", DEFAULT_BAIRRO);
        String cidade = lerString("Cidade (" + DEFAULT_CIDADE + "): ", DEFAULT_CIDADE);
        String estado = lerString("Estado (" + DEFAULT_ESTADO + "): ", DEFAULT_ESTADO);
        String cep = lerString("CEP (" + DEFAULT_CEP + "): ", DEFAULT_CEP);
        String tipoEndereco = lerString("Tipo Endereco (RESIDENCIAL): ", "RESIDENCIAL");
        String tipoCliente = lerString("Tipo Cliente (RESIDENCIAL): ", "RESIDENCIAL");

        try {
            var result = securePainelCagepaFacadeProxy.criarClientePf(
                    token, cpf, nome, email, telefone,
                    logradouro, numero, complemento, bairro, cidade, estado, cep,
                    tipoEndereco, tipoCliente
            );

            if (result != null) {
                System.out.println("Cliente PF criado com sucesso!");
                System.out.println("   CPF: " + result.cpf());
                System.out.println("   Nome: " + result.nome());
            } else {
                System.out.println("Falha ao criar cliente PF");
            }
        } catch (ValidationException e) {
            System.err.println("Erro de validacao: " + e.getMessage());
        }
    }

    private void criarClientePJ() {
        System.out.println("\n========== CRIAR CLIENTE (PESSOA JURIDICA) ==========");

        String cnpj = lerString("CNPJ (03.142.306/0001-70): ", "03.142.306/0001-70");
        String nomeFantasia = lerString("Nome Fantasia (Empresa X): ", "Empresa X");
        String razaoSocial = lerString("Razao Social (Empresa X Ltda): ", "Empresa X Ltda");
        String email = lerString("Email (email@cnpj.com): ", "email@cnpj.com");
        String telefone = lerString("Telefone (1188888-8888): ", "1188888-8888");
        String logradouro = lerString("Logradouro (Avenida Y): ", "Avenida Y");
        String numero = lerString("Numero (2000): ", "2000");
        String complemento = lerString("Complemento (Sala 202): ", "Sala 202");
        String bairro = lerString("Bairro (Bairro Z): ", "Bairro Z");
        String cidade = lerString("Cidade (Cidade W): ", "Cidade W");
        String estado = lerString("Estado (PB): ", "PB");
        String cep = lerString("CEP (87654-321): ", "87654-321");
        String tipoEndereco = lerString("Tipo Endereco (COMERCIAL): ", "COMERCIAL");
        String tipoCliente = lerString("Tipo Cliente (COMERCIAL): ", "COMERCIAL");

        try {
            var result = securePainelCagepaFacadeProxy.criarClientePj(
                    token, cnpj, nomeFantasia, razaoSocial, email, telefone,
                    logradouro, numero, complemento, bairro, cidade, estado, cep,
                    tipoEndereco, tipoCliente
            );

            if (result != null) {
                System.out.println("Cliente PJ criado com sucesso!");
                System.out.println("   CNPJ: " + result.cnpj());
                System.out.println("   Nome Fantasia: " + result.nomeFantasia());
            } else {
                System.out.println("Falha ao criar cliente PJ");
            }
        } catch (ValidationException e) {
            System.err.println("Erro de validacao: " + e.getMessage());
        }
    }

    private void registrarHidrometro() {
        System.out.println("\n========== REGISTRAR HIDROMETRO ==========");

        String idSha = lerString("ID SHA (SHATESTE1): ", "SHATESTE1");
        String status = lerString("Status (ATIVO): ", "ATIVO");
        int limiteConsumo = lerInteiro("Limite Consumo Mensal (15): ");
        if (limiteConsumo == -1) limiteConsumo = 15;

        String clienteCpfCnpj = lerString("CPF/CNPJ do Cliente (949.545.430-10): ", "949.545.430-10");
        String logradouro = lerString("Logradouro (" + DEFAULT_LOGRADOURO + "): ", DEFAULT_LOGRADOURO);
        String numero = lerString("Numero (" + DEFAULT_NUMERO + "): ", DEFAULT_NUMERO);
        String complemento = lerString("Complemento (" + DEFAULT_COMPLEMENTO + "): ", DEFAULT_COMPLEMENTO);
        String bairro = lerString("Bairro (" + DEFAULT_BAIRRO + "): ", DEFAULT_BAIRRO);
        String cidade = lerString("Cidade (" + DEFAULT_CIDADE + "): ", DEFAULT_CIDADE);
        String estado = lerString("Estado (" + DEFAULT_ESTADO + "): ", DEFAULT_ESTADO);
        String cep = lerString("CEP (" + DEFAULT_CEP + "): ", DEFAULT_CEP);
        String tipoEndereco = lerString("Tipo Endereco (RESIDENCIAL): ", "RESIDENCIAL");

        try {
            var result = securePainelCagepaFacadeProxy.registrarHidrometro(
                    token, idSha, LocalDate.now(), status, limiteConsumo,
                    clienteCpfCnpj, logradouro, numero, complemento, bairro, cidade, estado, cep,
                    tipoEndereco
            );

            if (result != null) {
                System.out.println("Hidrometro registrado com sucesso!");
                System.out.println("   SHA: " + result.idSha());
                System.out.println("   Status: " + result.status());
            } else {
                System.out.println("Falha ao registrar hidrometro");
            }
        } catch (ValidationException e) {
            System.err.println("Erro de validacao: " + e.getMessage());
        }
    }

    private void obterConsumoCliente() throws ValidationException {
        System.out.println("\n========== OBTER CONSUMO DE CLIENTE ==========");

        String clienteCpfCnpj = lerString("CPF/CNPJ do Cliente (949.545.430-10): ", "949.545.430-10");

        System.out.println("\nEscolha o periodo:");
        System.out.println("1 - Diario");
        System.out.println("2 - Semanal");
        System.out.println("3 - Mensal");
        System.out.println("4 - Anual");

        int periodo = lerInteiro("Periodo (1): ");
        if (periodo == -1) periodo = 1;

        com.fachada.cagepa.fachadacagepa.domain.application.dtos.ConsumoClientePeriodoDTO consumo = null;
        String periodoStr = switch (periodo) {
            case 1 -> {
                consumo = securePainelCagepaFacadeProxy.obterConsumoDiario(token, clienteCpfCnpj);
                yield "Diário";
            }
            case 2 -> {
                consumo = securePainelCagepaFacadeProxy.obterConsumoSemanal(token, clienteCpfCnpj);
                yield "Semanal";
            }
            case 3 -> {
                consumo = securePainelCagepaFacadeProxy.obterConsumoMensal(token, clienteCpfCnpj);
                yield "Mensal";
            }
            case 4 -> {
                consumo = securePainelCagepaFacadeProxy.obterConsumoAnual(token, clienteCpfCnpj);
                yield "Anual";
            }
            default -> "";
        };

        if (consumo != null) {
            System.out.println("\nConsumo " + periodoStr + ":");
            System.out.println("   Cliente: " + consumo.nomeCliente());
            System.out.println("   Periodo: " + consumo.periodo());
            System.out.println("   Consumo Total: " + consumo.consumoTotalM3() + " m3");
            System.out.println("\n   Detalhes por Hidrometro:");
            consumo.consumoPorHidrometro().forEach(h ->
                    System.out.println("     - " + h.idHidrometro() + ": " + h.consumoM3() + " m3")
            );
        } else {
            System.out.println("Nenhum consumo encontrado para este cliente");
        }
    }

    private void criarNovoAdmin() {
        System.out.println("\n========== CRIAR NOVO ADMIN ==========");

        String username = lerString("Username: ", "");
        String password = lerString("Password: ", "");

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username e Password sao obrigatorios!");
            return;
        }

        boolean result = securePainelCagepaFacadeProxy.criarAdmin(token, username, password);

        if (result) {
            System.out.println("Admin criado com sucesso!");
            System.out.println("   Username: " + username);
        } else {
            System.out.println("Falha ao criar admin");
        }
    }

    private void verHistoricoAuditoria() {
        System.out.println("\n========== HISTORICO DE AUDITORIA ==========");

        String historico = securePainelCagepaFacadeProxy.obterHistoricoAuditoria(token);
        System.out.println(historico);

        System.out.println("\nDeseja limpar o historico? (s/n)");
        String resposta = lerString(": ", "n");
        if (resposta.equalsIgnoreCase("s")) {
            boolean result = securePainelCagepaFacadeProxy.limparHistoricoAuditoria(token);
            if (result) {
                System.out.println("Historico limpo com sucesso!");
            } else {
                System.out.println("Falha ao limpar historico");
            }
        }
    }

    private void configurarSistema() {
        System.out.println("\n========== CONFIGURACOES DO SISTEMA ==========");

        System.out.println(securePainelCagepaFacadeProxy.exibirConfiguracaoAtual(token));

        System.out.println("\nEscolha uma opcao:");
        System.out.println("1 - Alterar Diretorio de Imagens");
        System.out.println("2 - Alterar Caminho Tesseract");
        System.out.println("0 - Voltar ao Menu");

        int opcao = lerInteiro("Opcao: ");

        switch (opcao) {
            case 1:
                alterarDiretorioImagens();
                break;
            case 2:
                alterarCaminhoTesseract();
                break;
            case 0:
                break;
            default:
                System.out.println("Opcao invalida!");
        }
    }

    private void alterarDiretorioImagens() {
        System.out.println("\n========== ALTERAR DIRETORIO DE IMAGENS ==========");
        String novoDir = lerString("Informe o novo diretorio: ", "");

        if (novoDir.isEmpty()) {
            System.out.println("Diretorio nao pode estar vazio!");
            return;
        }

        boolean resultado = securePainelCagepaFacadeProxy.configurarDiretorioImagens(token, novoDir);

        if (resultado) {
            System.out.println("Diretorio alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar diretorio. Verifique se o caminho existe.");
        }
    }

    private void alterarCaminhoTesseract() {
        System.out.println("\n========== ALTERAR CAMINHO TESSERACT ==========");
        System.out.println("Exemplos de caminho:");
        System.out.println("  Linux: /usr/share/tesseract-ocr/5/tessdata");
        System.out.println("  Windows: C:\\Program Files (x86)\\Tesseract-OCR\\tessdata");

        String novoPath = lerString("Informe o novo caminho Tesseract: ", "");

        if (novoPath.isEmpty()) {
            System.out.println("Caminho nao pode estar vazio!");
            return;
        }

        boolean resultado = securePainelCagepaFacadeProxy.configurarCaminhoTesseract(token, novoPath);

        if (resultado) {
            System.out.println("Caminho Tesseract alterado com sucesso!");
        } else {
            System.out.println("Erro ao alterar caminho Tesseract.");
        }
    }

    private void verRelatorioNotificacoes() {
        System.out.println("\n========== RELATORIO DE NOTIFICACOES ==========");

        String relatorio = securePainelCagepaFacadeProxy.obterRelatorioNotificacoes(token);
        System.out.println(relatorio);
    }

    private void desativarAdmin() {
        System.out.println("\n========== DESATIVAR ADMINISTRADOR ==========");

        String username = lerString("Username do admin a desativar: ", "");

        if (username.isEmpty()) {
            System.out.println("Username nao pode estar vazio!");
            return;
        }

        String confirmacao = lerString("Tem certeza que deseja desativar o admin '" + username + "'? (s/n): ", "n");

        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Operacao cancelada.");
            return;
        }

        try {
            boolean resultado = securePainelCagepaFacadeProxy.desativarAdmin(token, username);

            if (resultado) {
                System.out.println("Admin '" + username + "' desativado com sucesso!");
            } else {
                System.out.println("Falha ao desativar admin.");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private void listarClientes() {
        System.out.println("\n========== LISTAR CLIENTES ==========");

        try {
            var clientes = securePainelCagepaFacadeProxy.listarClientes(token);

            if (clientes == null || clientes.isEmpty()) {
                System.out.println("Nenhum cliente cadastrado.");
                return;
            }

            System.out.println("\nTotal de clientes: " + clientes.size());
            System.out.println("=====================================");

            for (var cliente : clientes) {
                System.out.println("\nCPF/CNPJ: " + cliente.getCpfCnpj());
                System.out.println("Nome: " + (cliente.getNomeCompleto() != null ? cliente.getNomeCompleto() : cliente.getNomeFantasia()));
                System.out.println("Email: " + cliente.getEmail());
                System.out.println("Telefone: " + cliente.getTelefone());
                System.out.println("Tipo: " + cliente.getTipoCliente());
                System.out.println("Status: " + (cliente.getAtivo() ? "ATIVO" : "INATIVO"));
                System.out.println("-------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private String lerString(String prompt, String defaultValue) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? defaultValue : input;
    }

    private int lerInteiro(String prompt) {
        System.out.print(prompt);
        try {
            String input = scanner.nextLine().trim();
            return input.isEmpty() ? -1 : Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void obterClienteCompleto() {
        System.out.println("\n========== OBTER CLIENTE COMPLETO ==========");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", DEFAULT_CPF);
        
        try {
            var cliente = securePainelCagepaFacadeProxy.obterClientePorCpfCnpj(token, cpfCnpj);
            
            if (cliente == null) {
                System.out.println("Cliente nao encontrado!");
                return;
            }
            
            System.out.println("\n===== DADOS DO CLIENTE =====");
            System.out.println("CPF/CNPJ: " + cliente.getCpfCnpj());
            System.out.println("Nome Completo: " + cliente.getNomeCompleto());
            System.out.println("Nome Fantasia: " + cliente.getNomeFantasia());
            System.out.println("Razao Social: " + cliente.getRazaoSocial());
            System.out.println("Email: " + cliente.getEmail());
            System.out.println("Telefone: " + cliente.getTelefone());
            System.out.println("Tipo: " + cliente.getTipoCliente());
            System.out.println("Status: " + (cliente.getAtivo() ? "ATIVO" : "INATIVO"));
            System.out.println("Data Cadastro: " + cliente.getDataCadastro());
            
            System.out.println("\n===== ENDERECOS =====");
            if (cliente.getEnderecos() != null && !cliente.getEnderecos().isEmpty()) {
                for (var endereco : cliente.getEnderecos()) {
                    System.out.println("\n- " + endereco.getLogradouro() + ", " + endereco.getNumero());
                    System.out.println("  Complemento: " + endereco.getComplemento());
                    System.out.println("  Bairro: " + endereco.getBairro());
                    System.out.println("  Cidade: " + endereco.getCidade());
                    System.out.println("  Estado: " + endereco.getEstado());
                    System.out.println("  CEP: " + endereco.getCep());
                }
            } else {
                System.out.println("Nenhum endereco cadastrado.");
            }
            
            System.out.println("\n===== HIDROMETROS =====");
            if (cliente.getHidrometros() != null && !cliente.getHidrometros().isEmpty()) {
                for (var hidrometro : cliente.getHidrometros()) {
                    System.out.println("\n- SHA ID: " + hidrometro.getIdSha());
                    System.out.println("  Status: " + hidrometro.getStatus());
                    System.out.println("  Limite Mensal: " + hidrometro.getLimiteConsumoMensalM3() + " m3");
                    System.out.println("  Data Instalacao: " + hidrometro.getDataInstalacao());
                }
            } else {
                System.out.println("Nenhum hidrometro cadastrado.");
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao obter cliente: " + e.getMessage());
        }
    }

    private void adicionarEnderecoCliente() {
        System.out.println("\n========== ADICIONAR ENDERECO A CLIENTE =====");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", DEFAULT_CPF);
        String logradouro = lerString("Logradouro: ", DEFAULT_LOGRADOURO);
        String numero = lerString("Numero: ", DEFAULT_NUMERO);
        String complemento = lerString("Complemento (opcional): ", DEFAULT_COMPLEMENTO);
        String bairro = lerString("Bairro: ", DEFAULT_BAIRRO);
        String cidade = lerString("Cidade: ", DEFAULT_CIDADE);
        String estado = lerString("Estado: ", DEFAULT_ESTADO);
        String cep = lerString("CEP: ", DEFAULT_CEP);
        
        try {
            boolean sucesso = securePainelCagepaFacadeProxy.adicionarEnderecoCliente(token, cpfCnpj, 
                logradouro, numero, complemento, bairro, cidade, estado, cep);
            
            if (sucesso) {
                System.out.println("\nEndereco adicionado com sucesso!");
            } else {
                System.out.println("\nFalha ao adicionar endereco!");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private void listarHidrometrosPorCliente() {
        System.out.println("\n========== LISTAR HIDROMETROS POR CLIENTE =====");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", DEFAULT_CPF);
        
        try {
            var hidrometros = securePainelCagepaFacadeProxy.obterHidrometrosPorCliente(token, cpfCnpj);
            
            if (hidrometros == null || hidrometros.isEmpty()) {
                System.out.println("Nenhum hidrometro cadastrado para este cliente.");
                return;
            }
            
            System.out.println("\nTotal de hidrometros: " + hidrometros.size());
            System.out.println("====================================");
            
            for (var hidrometro : hidrometros) {
                System.out.println("\nSHA ID: " + hidrometro.getIdSha());
                System.out.println("Status: " + hidrometro.getStatus());
                System.out.println("Limite Mensal: " + hidrometro.getLimiteConsumoMensalM3() + " m3");
                System.out.println("Data Instalacao: " + hidrometro.getDataInstalacao());
                System.out.println("Endereco: " + hidrometro.getEnderecoInstalacao().getLogradouro() + 
                    ", " + hidrometro.getEnderecoInstalacao().getNumero());
                System.out.println("------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private void alterarStatusHidrometro() {
        System.out.println("\n========== ATIVAR/DESATIVAR HIDROMETRO =====");
        
        String shaId = lerString("SHA ID do hidrometro: ", "");
        String ativoStr = lerString("Ativar (S/N): ", "S");
        boolean ativo = ativoStr.equalsIgnoreCase("S") || ativoStr.equalsIgnoreCase("SIM");
        
        try {
            boolean sucesso = securePainelCagepaFacadeProxy.alterarStatusHidrometro(token, shaId, ativo);
            
            if (sucesso) {
                System.out.println("\nStatus do hidrometro alterado com sucesso para: " + 
                    (ativo ? "ATIVO" : "INATIVO"));
            } else {
                System.out.println("\nFalha ao alterar status do hidrometro!");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    private void buscarHidrometroPorSha() {
        System.out.println("\n========== BUSCAR HIDROMETRO POR SHA =====");
        
        String idSha = lerString("SHA ID do hidrometro: ", "");
        
        try {
            Hidrometro hidrometro = securePainelCagepaFacadeProxy.obterHidrometroPorSha(token, idSha);
            
            System.out.println("------------------------------------");
            System.out.println("Hidrometro encontrado:");
            System.out.println("SHA ID: " + hidrometro.getIdSha());
            System.out.println("Status: " + (hidrometro.getStatus() != null ? hidrometro.getStatus() : "INATIVO"));
            System.out.println("Limite Mensal: " + hidrometro.getLimiteConsumoMensalM3() + " m3");
            System.out.println("Data Instalacao: " + hidrometro.getDataInstalacao());
            if (hidrometro.getEnderecoInstalacao() != null) {
                System.out.println("Endereco: " + hidrometro.getEnderecoInstalacao().getLogradouro() + 
                    ", " + hidrometro.getEnderecoInstalacao().getNumero());
            }
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void obterConsumoIndividualPorHidrometro() {
        System.out.println("\n========== CONSUMO INDIVIDUAL POR HIDROMETRO =====");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", "");
        
        try {
            List<ConsumoHidrometroDTO> consumos = securePainelCagepaFacadeProxy.obterConsumoIndividualPorHidrometro(token, cpfCnpj);
            
            if (consumos == null || consumos.isEmpty()) {
                System.out.println("Nenhum consumo encontrado para este cliente.");
                return;
            }
            
            System.out.println("------------------------------------");
            System.out.println("Consumos por Hidrometro:");
            System.out.println("CPF/CNPJ: " + cpfCnpj);
            System.out.println();
            for (ConsumoHidrometroDTO consumo : consumos) {
                System.out.println("  Hidrometro ID: " + consumo.idHidrometro());
                System.out.println("  Consumo: " + consumo.consumoM3() + " m3");
                System.out.println();
            }
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void obterConsumoTotalCliente() {
        System.out.println("\n========== CONSUMO TOTAL DO CLIENTE =====");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", "");
        
        try {
            Integer consumoTotal = securePainelCagepaFacadeProxy.obterConsumoTotalCliente(token, cpfCnpj);
            
            System.out.println("------------------------------------");
            System.out.println("Consumo Total:");
            System.out.println("CPF/CNPJ: " + cpfCnpj);
            System.out.println("Consumo Total: " + (consumoTotal != null ? consumoTotal : 0) + " m3");
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void listarEmailsNotificacoes() {
        System.out.println("\n========== LISTAR EMAILS DE NOTIFICACOES =====");
        
        try {
            List<Notificacao> notificacoes = securePainelCagepaFacadeProxy.obterEmailsNotificacoes(token);
            
            if (notificacoes == null || notificacoes.isEmpty()) {
                System.out.println("Nenhuma notificacao encontrada.");
                return;
            }
            
            System.out.println("------------------------------------");
            System.out.println("Emails de Notificacoes Enviadas (ultimos 30 dias):");
            System.out.println();
            for (Notificacao notif : notificacoes) {
                System.out.println("  Cliente: " + (notif.getClienteCpfCnpj() != null ? notif.getClienteCpfCnpj() : "N/A"));
                System.out.println("  Email: " + (notif.getClienteEmail() != null ? notif.getClienteEmail() : "N/A"));
                System.out.println("  Assunto: " + (notif.getAssunto() != null ? notif.getAssunto() : "N/A"));
                System.out.println("  Mensagem: " + (notif.getMensagem() != null ? notif.getMensagem() : "N/A"));
                System.out.println("  Data Envio: " + (notif.getDataEnvio() != null ? notif.getDataEnvio() : "N/A"));
                System.out.println("  Status: " + (notif.getStatusEnvio() != null ? notif.getStatusEnvio() : "N/A"));
                System.out.println();
            }
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void obterHistoricoNotificacoes() {
        System.out.println("\n========== HISTORICO DE NOTIFICACOES =====");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", "");
        
        try {
            List<Notificacao> notificacoes = securePainelCagepaFacadeProxy.obterHistoricoNotificacoes(token, cpfCnpj);
            
            if (notificacoes == null || notificacoes.isEmpty()) {
                System.out.println("Nenhuma notificacao encontrada para este cliente.");
                return;
            }
            
            System.out.println("------------------------------------");
            System.out.println("Historico de Notificacoes:");
            System.out.println("CPF/CNPJ: " + cpfCnpj);
            System.out.println();
            for (Notificacao notif : notificacoes) {
                System.out.println("  Hidrometro ID: " + (notif.getHidrometroIdSha() != null ? notif.getHidrometroIdSha() : "N/A"));
                System.out.println("  Assunto: " + (notif.getAssunto() != null ? notif.getAssunto() : "N/A"));
                System.out.println("  Data Envio: " + (notif.getDataEnvio() != null ? notif.getDataEnvio() : "N/A"));
                System.out.println("  Status: " + (notif.getStatusEnvio() != null ? notif.getStatusEnvio() : "N/A"));
                System.out.println();
            }
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void validarNotificacaoDuplicada() {
        System.out.println("\n========== VALIDAR NOTIFICACAO DUPLICADA =====");
        
        String cpfCnpj = lerString("CPF/CNPJ do cliente: ", "");
        String idSha = lerString("SHA ID do hidrometro: ", "");
        
        try {
            Boolean podeEnviar = securePainelCagepaFacadeProxy.podeEnviarNotificacao(token, cpfCnpj, idSha);
            
            System.out.println("------------------------------------");
            System.out.println("Validacao de Notificacao:");
            System.out.println("CPF/CNPJ: " + cpfCnpj);
            System.out.println("Hidrometro ID: " + idSha);
            System.out.println();
            if (podeEnviar != null && podeEnviar) {
                System.out.println("STATUS: Pode enviar notificacao");
            } else {
                System.out.println("STATUS: Notificacao ja foi enviada hoje (duplicata)");
            }
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void exibirAuditoriaCompleta() {
        System.out.println("\n========== AUDITORIA COMPLETA =====");
        
        try {
            List<AuditEntry> auditorias = securePainelCagepaFacadeProxy.obterHistoricoAuditoriaCompleto(token);
            
            if (auditorias == null || auditorias.isEmpty()) {
                System.out.println("Nenhum registro de auditoria encontrado.");
                return;
            }
            
            System.out.println("------------------------------------");
            System.out.println("Historico de Auditoria (CRUD):");
            System.out.println();
            for (AuditEntry audit : auditorias) {
                System.out.println("  Timestamp: " + (audit.getTimestamp() != null ? audit.getTimestamp() : "N/A"));
                System.out.println("  Usuario: " + (audit.getUsername() != null ? audit.getUsername() : "N/A"));
                System.out.println("  Operacao: " + (audit.getOperacao() != null ? audit.getOperacao() : "N/A"));
                System.out.println("  Entidade: " + (audit.getEntidade() != null ? audit.getEntidade() : "N/A"));
                System.out.println("  Detalhes: " + (audit.getDetalhes() != null ? audit.getDetalhes() : "N/A"));
                System.out.println("  Resultado: " + (audit.getResultado() != null ? audit.getResultado() : "N/A"));
                System.out.println();
            }
            System.out.println("------------------------------------");
        } catch (ValidationException e) {
            System.err.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void encerrarAplicacao() {
        try {
            if (scanner != null) {
                scanner.close();
            }
            System.out.println("Encerrando Spring Boot...");
            if (applicationContext != null) {
                SpringApplication.exit(applicationContext);
            } else {
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Erro ao encerrar: " + e.getMessage());
            System.exit(1);
        }
    }
}
