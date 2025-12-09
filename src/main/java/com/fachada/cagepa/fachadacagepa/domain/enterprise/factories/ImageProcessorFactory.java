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

    public HidromeImageProcessor getProcessor(String fileName) throws Exception {
        if (proprietarioImageProcessor.supports(fileName)) {
            return proprietarioImageProcessor;
        } else if (colaboradorImageProcessor.supports(fileName)) {
            return colaboradorImageProcessor;
        } else {
            throw new IllegalArgumentException("Nenhum processador de imagem suportado para o arquivo: " + fileName);
        }
    }
}
