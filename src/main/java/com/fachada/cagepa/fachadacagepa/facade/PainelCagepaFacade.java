package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ImageProcessorFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.HidromeImageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PainelCagepaFacade {

    @Autowired
    private ImageProcessorFactory imageProcessorFactory;

    public boolean authenticate(String username, String password) {
        return "admin".equals(username) && "admin".equals(password);
    }

    public void processarImagens(String token) throws Exception {
        String[] files = {
                "/home/apolo/Documents/diretorio_compartilhado_shas/cliente_0001_prop.png",
                "/home/apolo/Documents/diretorio_compartilhado_shas/cliente_0001_col.png"
        };

        for (String file : files) {
            HidromeImageProcessor processor = imageProcessorFactory.getProcessor(file);
            System.out.printf("Processando imagens: %s\n", file);
            String valor = processor.extractReading(file);
            System.out.println("Valor: " + valor);
        }
    }
}
