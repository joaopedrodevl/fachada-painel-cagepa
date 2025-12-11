package com.fachada.cagepa.fachadacagepa;

import com.fachada.cagepa.fachadacagepa.facade.proxy.SecurePainelCagepaFacadeProxy;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.validation.ValidationException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDate;
import java.util.Scanner;

@SpringBootApplication
public class FachadaCagepaApplication implements CommandLineRunner {

    private final SecurePainelCagepaFacadeProxy securePainelCagepaFacadeProxy;

    @Value("${app.cli.enabled:true}")
    private boolean cliEnabled;

    private Scanner scanner;
    private String token;

    // Valores padrão para inputs
    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "123456";
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
        SpringApplication.run(FachadaCagepaApplication.class, args);
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (!cliEnabled) {
            return;
        }

        scanner = new Scanner(System.in);

        System.out.println("========== CLIENTE CLI - PAINEL CAGEPA ==========\n");
        System.out.println("Realizando login com credenciais padrao...");
        token = securePainelCagepaFacadeProxy.login(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        if (token == null) {
            System.err.println("Falha ao fazer login");
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
                        verHistoricoAuditoria();
                        break;
                    case 7:
                        configurarSistema();
                        break;
                    case 8:
                        securePainelCagepaFacadeProxy.verificarENotificarConsumoAlto(token);
                        System.out.println("Verificacao de consumo concluida!");
                        break;
                    case 9:
                        verRelatorioNotificacoes();
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Ate logo!");
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
        System.out.println("6 - Ver Historico de Auditoria");
        System.out.println("7 - Configuracoes do Sistema");
        System.out.println("8 - Verificar e Notificar Consumo Alto");
        System.out.println("9 - Ver Relatorio de Notificacoes");
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
}
