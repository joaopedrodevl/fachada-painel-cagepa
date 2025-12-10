package com.fachada.cagepa.fachadacagepa.config;

import java.io.IOException;

public class SystemConfiguration {
    private static SystemConfiguration instance;
    private String imageDirectory;
    private String tessDataPath;
    private String osType;
    private ConfigFileManager configManager;

    private SystemConfiguration() throws IOException {
        this.configManager = new ConfigFileManager();
        this.imageDirectory = configManager.getImageDirectory();
        this.tessDataPath = configManager.getTessDataPath();
        this.osType = TessDataPathFactory.detectOS();
    }

    public synchronized static SystemConfiguration getInstance() throws IOException {
        if (instance == null) {
            instance = new SystemConfiguration();
        }
        return instance;
    }

    public String getImageDirectory() {
        return imageDirectory;
    }

    public String getTessDataPath() {
        return tessDataPath;
    }

    public String getOsType() {
        return osType;
    }

    public void setImageDirectory(String imageDirectory) throws IOException {
        configManager.setImageDirectory(imageDirectory);
        this.imageDirectory = imageDirectory;
    }

    public void setTessDataPath(String tessDataPath) throws IOException {
        configManager.setTessDataPath(tessDataPath);
        this.tessDataPath = tessDataPath;
    }
}
