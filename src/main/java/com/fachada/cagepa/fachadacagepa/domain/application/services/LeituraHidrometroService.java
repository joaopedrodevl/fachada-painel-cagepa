package com.fachada.cagepa.fachadacagepa.domain.application.services;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.entity.LeituraHidrometro;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.enums.StatusHidrometro;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.ImageProcessorFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.factories.LeituraHidrometroFactory;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy.HidromeImageProcessor;
import com.fachada.cagepa.fachadacagepa.infra.persistence.IHidrometroJpaRepository;
import com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometroRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;


@Service
public class LeituraHidrometroService {
    @Autowired
    private LeituraHidrometroRepositoryImpl leituraHidrometroRepository;

    @Autowired
    private ImageProcessorFactory processorFactory;
    @Autowired
    private LeituraHidrometroFactory leituraHidrometroFactory;
    @Autowired
    private IHidrometroJpaRepository hidrometroJpaRepository;

    @Transactional
    public void salvarLeitura(LeituraHidrometro leitura) {
        var hidrometroEntity = hidrometroJpaRepository.findById(leitura.getHidrometroId())
            .orElseThrow(() -> new IllegalArgumentException("Hidrometro não encontrado"));

        if (hidrometroEntity.getStatus() == StatusHidrometro.INATIVO) {
            throw new IllegalStateException("Não é possível salvar leitura para hidrometro inativo");
        }

        // Validar que o valor da leitura é maior que a última leitura registrada (se houver)
        var ultimaLeitura = leituraHidrometroRepository.pegarUltimaLeitura(leitura.getHidrometroId());
        if (ultimaLeitura.isPresent() && Objects.equals(leitura.getValor(), ultimaLeitura.get())) {
            return;
        }

        com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometro leituraHidrometro = new com.fachada.cagepa.fachadacagepa.infra.persistence.LeituraHidrometro(
          null, hidrometroEntity, leitura.getValor(), leitura.getDataLeitura(), leitura.getStatus()
        );

        leituraHidrometroRepository.save(leituraHidrometro);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processImage(String imagePath) throws Exception {
        try {
            var shaId = getShaIdFromImagePath(imagePath);
            var hidrometro = hidrometroJpaRepository.findById(shaId)
                    .orElseThrow(() -> new IllegalArgumentException("Hidrometro com SHA ID " + shaId + " não encontrado"));

            if (hidrometro.getStatus() == StatusHidrometro.INATIVO) {
                throw new IllegalStateException("Não é possível salvar leitura para hidrometro inativo");
            }

            HidromeImageProcessor processor = processorFactory.getProcessor(imagePath);
            String valor2 = processor.extractReading(imagePath);
            LeituraHidrometro entity = leituraHidrometroFactory.criar(shaId, valor2);
            this.salvarLeitura(entity);

        } catch (IllegalStateException e) {
            System.err.println("Erro de estado ao processar imagem " + imagePath + ": " + e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao localizar recursos para imagem " + imagePath + ": " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Erro inesperado ao processar imagem " + imagePath + ": " + e.getMessage());
            throw e;
        }
    }

    private String getShaIdFromImagePath(String imagePath) {
        String fileName = imagePath.substring(imagePath.lastIndexOf('/') + 1);
        String nameWithoutExtension = fileName.substring(0, fileName.lastIndexOf('.'));
        return nameWithoutExtension.split("_")[0];
    }
}
