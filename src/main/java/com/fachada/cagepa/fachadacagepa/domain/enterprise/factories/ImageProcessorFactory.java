package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.ColaboradorImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.HidromeImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.ProprietarioImageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageProcessorFactory {
    @Autowired
    private ProprietarioImageProcessor  proprietarioImageProcessor;

    @Autowired
    private ColaboradorImageProcessor colaboradorImageProcessor;

    public HidromeImageProcessor getProcessor(String fileName){
        if (fileName.contains("prop") || fileName.startsWith("prop_")) {
            return proprietarioImageProcessor;
        } else if (fileName.contains("col") || fileName.startsWith("col_")) {
            return colaboradorImageProcessor;
        } else {
            throw new IllegalArgumentException("Fonte de imagem desconhecida: " + fileName);
        }
    }
}
