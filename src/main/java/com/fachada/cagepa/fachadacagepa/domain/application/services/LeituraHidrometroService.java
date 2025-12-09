package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.entity.LeituraHidrometro;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ImageProcessorFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.LeituraHidrometroFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.HidromeImageProcessor;
import com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometroRepositoryImpl;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeituraHidrometroService {
    @Autowired
    private LeituraHidrometroRepositoryImpl leituraHidrometroRepository;

    @Autowired
    private ImageProcessorFactory processorFactory;
    @Autowired
    private LeituraHidrometroFactory leituraHidrometroFactory;

    @Transactional
    public void salvarLeitura(LeituraHidrometro leitura) {
        com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometro leituraHidrometro = new com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometro(
          null, leitura.getClienteId(), leitura.getValor(), leitura.getDataLeitura(), leitura.getStatus()
        );

        leituraHidrometroRepository.save(leituraHidrometro);
    }

    @Transactional
    public LeituraHidrometro processImage(String imagePath) throws Exception {
        HidromeImageProcessor processor = processorFactory.getProcessor(imagePath);
        String valor2 = processor.extractReading(imagePath);
        LeituraHidrometro entity = leituraHidrometroFactory.criar("cliente_0001", valor2);
        this.salvarLeitura(entity);
        return entity;
    }
}
