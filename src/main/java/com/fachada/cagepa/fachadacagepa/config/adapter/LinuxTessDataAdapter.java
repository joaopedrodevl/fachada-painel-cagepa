package com.fachada.cagepa.fachadacagepa.config.adapter;

import com.fachada.cagepa.fachadacagepa.config.TessDataPathAdapter;

public class LinuxTessDataAdapter implements TessDataPathAdapter {

    private String tessDataPath;

    public LinuxTessDataAdapter(String tessDataPath) {
        this.tessDataPath = tessDataPath;
    }

    @Override
    public String getTessDataPath() {
        return tessDataPath;
    }

    @Override
    public String getOsType() {
        return "LINUX";
    }
}


