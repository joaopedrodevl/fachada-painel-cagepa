package com.fachada.cagepa.fachadacagepa.config;

import com.fachada.cagepa.fachadacagepa.config.adapter.LinuxTessDataAdapter;
import com.fachada.cagepa.fachadacagepa.config.adapter.WindowsTessDataAdapter;

public class TessDataPathFactory {

    public static TessDataPathAdapter createAdapter(String tessdataPath) throws IllegalArgumentException {
        if (tessdataPath == null || tessdataPath.trim().isEmpty()) {
            throw new IllegalArgumentException("Caminho do Tesseract nao pode estar vazio");
        }

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return new WindowsTessDataAdapter(tessdataPath);
        } else if (osName.contains("linux") || osName.contains("nux")) {
            return new LinuxTessDataAdapter(tessdataPath);
        } else if (osName.contains("mac")) {
            return new LinuxTessDataAdapter(tessdataPath);
        } else {
            throw new IllegalArgumentException("Sistema operacional nao suportado: " + osName);
        }
    }

    public static String detectOS() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            return "WINDOWS";
        } else if (osName.contains("linux") || osName.contains("nux")) {
            return "LINUX";
        } else if (osName.contains("mac")) {
            return "MAC";
        } else {
            return "UNKNOWN";
        }
    }
}

