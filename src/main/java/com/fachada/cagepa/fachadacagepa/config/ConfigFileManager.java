package com.fachada.cagepa.fachadacagepa.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileManager {

    private static final String CONFIG_FILE = "fachada.config";
    private static final String IMAGE_DIR_KEY = "image.directory";
    private static final String TESS_PATH_KEY = "tesseract.path";

    private final Map<String, String> config = new HashMap<>();

    public ConfigFileManager() throws IOException {
        loadConfigFile();
    }

    private void loadConfigFile() throws IOException {
        Path configPath = Paths.get(CONFIG_FILE);

        if (!Files.exists(configPath)) {
            System.out.println("Arquivo de configuracao nao encontrado: " + CONFIG_FILE);
            System.out.println("Criando arquivo de configuracao padrao...");
            createDefaultConfigFile();
        } else {
            readConfigFile(configPath);
        }
    }

    private void createDefaultConfigFile() throws IOException {
        String os = TessDataPathFactory.detectOS();
        String defaultTessPath = os.equals("WINDOWS")
            ? "C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"
            : "/usr/share/tesseract-ocr/5/tessdata";

        String defaultConfig = IMAGE_DIR_KEY + "=/home/apolo/Documents/diretorio_compartilhado_shas\n" +
                               TESS_PATH_KEY + "=" + defaultTessPath + "\n";

        Path configPath = Paths.get(CONFIG_FILE);
        Files.write(configPath, defaultConfig.getBytes());
        readConfigFile(configPath);

        System.out.println("Arquivo de configuracao criado com sucesso: " + CONFIG_FILE);
        System.out.println("Sistema Operacional detectado: " + os);
    }

    private void readConfigFile(Path configPath) throws IOException {
        List<String> lines = Files.readAllLines(configPath);
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }

            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                config.put(key, value);
            }
        }
    }

    public String getImageDirectory() throws IOException {
        String imageDir = config.get(IMAGE_DIR_KEY);
        if (imageDir == null) {
            throw new IOException("Propriedade '" + IMAGE_DIR_KEY + "' nao encontrada em " + CONFIG_FILE);
        }

        File file = new File(imageDir);
        if (!file.exists()) {
            throw new IOException("Diretorio de imagens nao existe: " + imageDir);
        }

        return file.getCanonicalPath();
    }

    public String getTessDataPath() {
        String tessPath = config.get(TESS_PATH_KEY);
        if (tessPath == null) {
            String os = TessDataPathFactory.detectOS();
            tessPath = os.equals("WINDOWS")
                ? "C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"
                : "/usr/share/tesseract-ocr/5/tessdata";
        }
        return tessPath;
    }

    public void setImageDirectory(String imageDirectory) throws IOException {
        config.put(IMAGE_DIR_KEY, imageDirectory);
        saveConfigFile();
    }

    public void setTessDataPath(String tessDataPath) throws IOException {
        config.put(TESS_PATH_KEY, tessDataPath);
        saveConfigFile();
    }

    private void saveConfigFile() throws IOException {
        StringBuilder content = new StringBuilder();
        content.append("# Configuracao da Fachada Cagepa\n");
        content.append("# Diretorio de imagens para processamento\n");
        content.append(IMAGE_DIR_KEY).append("=").append(config.get(IMAGE_DIR_KEY)).append("\n");
        content.append("\n# Caminho do diretorio Tesseract (ex: /usr/share/tesseract-ocr/5/tessdata ou C:\\Program Files\\Tesseract-OCR\\tessdata)\n");
        content.append(TESS_PATH_KEY).append("=").append(config.get(TESS_PATH_KEY)).append("\n");

        Path configPath = Paths.get(CONFIG_FILE);
        Files.write(configPath, content.toString().getBytes());
    }
}

