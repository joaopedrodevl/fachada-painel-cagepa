package com.fachada.cagepa.fachadacagepa.config;

import java.io.File;
import java.io.IOException;

public class SystemConfiguration {
    private static SystemConfiguration instance;
    private String imageDirectory;
    private String tessDataPath;

    private SystemConfiguration() throws IOException {
        File file = new File("/home/apolo/Documents/diretorio_compartilhado_shas");
        this.imageDirectory = file.getCanonicalPath();
        // Caminho do Tesseract (ajuste conforme sua versão do Ubuntu)
        this.tessDataPath = "/usr/share/tesseract-ocr/5/tessdata";
    }

    public synchronized static SystemConfiguration getInstance() throws IOException {
        if (instance == null) {
            instance = new SystemConfiguration();
        }
        return instance;
    }

    public String getImageDirectory() { return imageDirectory; }
    public String getTessDataPath() { return tessDataPath; }
}
