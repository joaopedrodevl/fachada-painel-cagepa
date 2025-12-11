package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.config.SystemConfiguration;
import java.io.File;
import java.io.IOException;

/**
 * Façade para gerenciamento de configurações do sistema
 * Permitir que o administrador forneça o diretório compartilhado
 * Validar se o diretório é válido
 */
public class ConfigurationFacade {

    private final SystemConfiguration systemConfiguration;

    public ConfigurationFacade(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    /**
     * Obtém o diretório de imagens configurado
     */
    public String obterDiretorioImagens() throws IOException {
        return systemConfiguration.getImageDirectory();
    }

    /**
     * Obtém o caminho do Tesseract configurado
     */
    public String obterCaminhoTesseract() {
        return systemConfiguration.getTessDataPath();
    }

    /**
     * Obtém o sistema operacional detectado
     */
    public String obterSistemaOperacional() {
        return systemConfiguration.getOsType();
    }

    /**
     * Configura um novo diretório para monitoramento de imagens
     * Valida se o diretório é válido antes de configurar
     * @param novoDir Caminho do novo diretório
     * @throws IllegalArgumentException se diretório inválido
     * @throws IOException se erro ao salvar configuração
     */
    public void configurarDiretorioImagens(String novoDir) throws IllegalArgumentException, IOException {
        if (novoDir == null || novoDir.trim().isEmpty()) {
            throw new IllegalArgumentException("Diretório não pode ser vazio");
        }

        if (!validarDiretorio(novoDir)) {
            throw new IllegalArgumentException("Diretório inválido ou inacessível: " + novoDir);
        }

        systemConfiguration.setImageDirectory(novoDir);
        System.out.println("Diretório de imagens configurado com sucesso: " + novoDir);
    }

    /**
     * Configura um novo caminho para o Tesseract
     * @param novoPath Caminho do Tesseract
     * @throws IllegalArgumentException se caminho inválido
     * @throws IOException se erro ao salvar configuração
     */
    public void configurarCaminhoTesseract(String novoPath) throws IllegalArgumentException, IOException {
        if (novoPath == null || novoPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho do Tesseract não pode ser vazio");
        }

        systemConfiguration.setTessDataPath(novoPath);
        System.out.println("Caminho Tesseract configurado com sucesso: " + novoPath);
    }

    /**
     * Valida se um diretório existe e é acessível
     * @param caminhoDir Caminho do diretório a validar
     * @return true se válido, false caso contrário
     */
    public boolean validarDiretorio(String caminhoDir) {
        try {
            if (caminhoDir == null || caminhoDir.trim().isEmpty()) {
                return false;
            }

            File diretorio = new File(caminhoDir);

            // Verificar se o caminho existe
            if (!diretorio.exists()) {
                System.out.println("Diretório não existe: " + caminhoDir);
                return false;
            }

            // Verificar se é um diretório
            if (!diretorio.isDirectory()) {
                System.out.println("Caminho não é um diretório: " + caminhoDir);
                return false;
            }

            // Verificar se é possível ler do diretório
            if (!diretorio.canRead()) {
                System.out.println("Sem permissão de leitura no diretório: " + caminhoDir);
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erro ao validar diretório: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cria um novo diretório se não existir
     * @param caminhoDir Caminho do diretório a criar
     * @return true se criado com sucesso, false caso contrário
     */
    public boolean criarDiretorioSeNaoExistir(String caminhoDir) {
        try {
            File diretorio = new File(caminhoDir);
            if (!diretorio.exists()) {
                return diretorio.mkdirs();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao criar diretório: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exibe a configuração atual do sistema
     */
    public void exibirConfiguracaoAtual() throws IOException {
        System.out.println("\n========== CONFIGURACAO ATUAL ==========");
        System.out.println("Diretório de imagens: " + obterDiretorioImagens());
        System.out.println("Diretório válido: " + validarDiretorio(obterDiretorioImagens()));
        System.out.println("Caminho Tesseract: " + obterCaminhoTesseract());
        System.out.println("Sistema Operacional: " + obterSistemaOperacional());
        System.out.println("========================================");
    }

    /**
     * Obtém informações detalhadas do diretório de imagens
     * @return String com informações do diretório
     */
    public String obterInfoDiretorio(String caminhoDir) {
        try {
            if (!validarDiretorio(caminhoDir)) {
                return "Diretório inválido: " + caminhoDir;
            }

            File diretorio = new File(caminhoDir);
            File[] arquivos = diretorio.listFiles();
            int totalArquivos = arquivos != null ? arquivos.length : 0;

            StringBuilder info = new StringBuilder();
            info.append("Caminho: ").append(caminhoDir).append("\n");
            info.append("Existe: ").append(diretorio.exists()).append("\n");
            info.append("É diretório: ").append(diretorio.isDirectory()).append("\n");
            info.append("Pode ler: ").append(diretorio.canRead()).append("\n");
            info.append("Pode escrever: ").append(diretorio.canWrite()).append("\n");
            info.append("Total de arquivos: ").append(totalArquivos).append("\n");

            return info.toString();
        } catch (Exception e) {
            return "Erro ao obter informações: " + e.getMessage();
        }
    }
}

