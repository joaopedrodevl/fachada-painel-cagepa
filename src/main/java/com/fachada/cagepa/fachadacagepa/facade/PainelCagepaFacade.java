package com.fachada.cagepa.fachadacagepa.facade;

import com.fachada.cagepa.fachadacagepa.config.SystemConfiguration;
import com.fachada.cagepa.fachadacagepa.domain.application.services.LeituraHidrometroService;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.entity.LeituraHidrometro;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ImageProcessorFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.LeituraHidrometroFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.FachadaImageObserver;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.observer.ImageWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PainelCagepaFacade {

    @Autowired
    private ImageProcessorFactory imageProcessorFactory;

    @Autowired
    private LeituraHidrometroFactory leituraHidrometroFactory;

    @Autowired
    private LeituraHidrometroService leituraHidrometroService;

    private ImageWatcher imageWatcher;

    public PainelCagepaFacade(ImageProcessorFactory processorFactory, FachadaImageObserver observer) {
        this.imageProcessorFactory = processorFactory;
        try {
            String dir = SystemConfiguration.getInstance().getImageDirectory();
            this.imageWatcher = new ImageWatcher(dir);
            this.imageWatcher.addObserver(observer);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao inicializar ImageWatcher", e);
        }
    }

    public void iniciarMonitoramento() throws IOException {
        imageWatcher.startWatching();
        System.out.println("Monitoramento iniciado em: " + SystemConfiguration.getInstance().getImageDirectory());
    }

    public void pararMonitoramento() {
        if (imageWatcher != null) {
            imageWatcher.stopWatching();
        }
    }
}
