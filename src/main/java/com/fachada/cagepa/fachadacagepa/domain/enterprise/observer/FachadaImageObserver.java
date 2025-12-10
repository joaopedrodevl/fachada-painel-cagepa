package com.fachada.cagepa.fachadacagepa.domain.enterprise.observer;

import com.fachada.cagepa.fachadacagepa.domain.application.services.LeituraHidrometroService;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.interfaces.ImageObserver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;

@Component
public class FachadaImageObserver implements ImageObserver {
    private final LeituraHidrometroService leituraHidrometroService;

    public FachadaImageObserver(LeituraHidrometroService leituraHidrometroService) {
        this.leituraHidrometroService = leituraHidrometroService;
    }

    @Override
    @Transactional
    public void onImageEvent(String imagePath, Object eventType) {
        try {
            if (eventType == StandardWatchEventKinds.ENTRY_CREATE) {

                Thread.sleep(1000);

                File file = new File(imagePath);
                if (!file.exists() || file.length() == 0) {
                    System.err.println("Arquivo inacessível ou vazio: " + imagePath);
                    return;
                }

                try {
                    leituraHidrometroService.processImage(imagePath);
                } catch (IllegalArgumentException e) {
                    System.err.println("Erro de argumento ao processar imagem: " + imagePath);
                    System.err.println("Detalhes: " + e.getMessage());
                } catch (IllegalStateException e) {
                    System.err.println("Erro de estado ao processar imagem: " + imagePath);
                    System.err.println("Detalhes: " + e.getMessage());
                } catch (Exception e) {
                    System.err.println("Erro ao processar imagem: " + imagePath);
                    if (e.getMessage() != null && e.getMessage().contains("EntityManagerFactory is closed")) {
                        System.err.println("EntityManager foi fechado. A aplicação pode estar encerrando.");
                    } else {
                        System.err.println("Detalhes: " + e.getMessage());
                    }
                }
            }
        } catch (InterruptedException e) {
            System.err.println("Thread interrompida ao processar imagem: " + imagePath);
            System.err.println("Detalhes: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("Erro inesperado no Observer: " + imagePath);
            System.err.println("Detalhes: " + e.getMessage());
        }
    }
}
