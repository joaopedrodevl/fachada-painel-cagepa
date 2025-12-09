package com.fachada.cagepa.fachadacagepa.domain.enterprise.observer;

import com.fachada.cagepa.fachadacagepa.domain.application.services.LeituraHidrometroService;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.interfaces.ImageObserver;
import com.fachada.cagepa.fachadacagepa.facade.PainelCagepaFacade;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;

@Component
public class FachadaImageObserver implements ImageObserver {
    private final LeituraHidrometroService leituraHidrometroService;

    public FachadaImageObserver(LeituraHidrometroService leituraHidrometroService) {
        this.leituraHidrometroService = leituraHidrometroService;
    }

    @Override
    public void onImageEvent(String imagePath, Object eventType) {
        try {
            if (eventType == StandardWatchEventKinds.ENTRY_CREATE) {

                Thread.sleep(1000);

                File file = new File(imagePath);
                if (!file.exists() || file.length() == 0) {
                    System.err.println("Arquivo inacessível ou vazio: " + imagePath);
                    return;
                }

                leituraHidrometroService.processImage(imagePath);
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar imagem via Observer: " + imagePath);
            e.printStackTrace();
        }
    }
}
