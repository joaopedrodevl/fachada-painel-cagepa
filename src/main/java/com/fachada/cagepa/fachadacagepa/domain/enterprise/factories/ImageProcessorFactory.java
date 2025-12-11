package com.fachada.cagepa.fachadacagepa.domain.enterprise.factories;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.ColaboradorImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.HidromeImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.ProprietarioImageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageProcessorFactory {
    private final ProprietarioImageProcessor  proprietarioImageProcessor;

    private final ColaboradorImageProcessor colaboradorImageProcessor;

    public ImageProcessorFactory(ProprietarioImageProcessor proprietarioImageProcessor, ColaboradorImageProcessor colaboradorImageProcessor) {
        this.proprietarioImageProcessor = proprietarioImageProcessor;
        this.colaboradorImageProcessor = colaboradorImageProcessor;
    }

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
