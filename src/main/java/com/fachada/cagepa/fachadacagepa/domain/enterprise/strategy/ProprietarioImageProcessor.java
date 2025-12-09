package com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.template.ColaboradorSixDigitImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.template.ImageProcessingTemplate;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.template.SixDigitImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.util.ImageDigitExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProprietarioImageProcessor implements HidromeImageProcessor{

    @Autowired
    private ImageDigitExtractor imageDigitExtractor;

    @Override
    public String extractReading(String imagePath) throws Exception{
        ImageProcessingTemplate template = new SixDigitImageProcessor();
        return template.processImage(imagePath);
    }

    @Override
    public boolean supports(String imagePath) throws Exception {
        try {
            ImageProcessingTemplate template = new SixDigitImageProcessor();
            String value = template.processImage(imagePath);
            return value.matches("\\d{6}");
        } catch (Exception e) {
            return false;
        }
    }
}
