package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.config.SystemConfiguration;
import java.io.IOException;

public class ConfigurationFacade {

    private final SystemConfiguration systemConfiguration;

    public ConfigurationFacade(SystemConfiguration systemConfiguration) {
        this.systemConfiguration = systemConfiguration;
    }

    public String obterDiretorioImagens() throws IOException {
        return systemConfiguration.getImageDirectory();
    }

    public String obterCaminhoTesseract() {
        return systemConfiguration.getTessDataPath();
    }

    public String obterSistemaOperacional() {
        return systemConfiguration.getOsType();
    }

    public void configurarDiretorioImagens(String novoDir) throws IOException {
        systemConfiguration.setImageDirectory(novoDir);
        System.out.println("Diretorio de imagens configurado com sucesso: " + novoDir);
    }

    public void configurarCaminhoTesseract(String novoPath) throws IOException {
        systemConfiguration.setTessDataPath(novoPath);
        System.out.println("Caminho Tesseract configurado com sucesso: " + novoPath);
    }

    public void exibirConfiguracaoAtual() throws IOException {
        System.out.println("\n========== CONFIGURACAO ATUAL ==========");
        System.out.println("Diretorio de imagens: " + obterDiretorioImagens());
        System.out.println("Caminho Tesseract: " + obterCaminhoTesseract());
        System.out.println("Sistema Operacional: " + obterSistemaOperacional());
        System.out.println("========================================");
    }
}

