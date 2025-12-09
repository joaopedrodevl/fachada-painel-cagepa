package com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy;

import com.fachada.cagepa.fachadacagepa.domain.enterprise.template.ColaboradorSixDigitImageProcessor;
import com.fachada.cagepa.fachadacagepa.domain.enterprise.template.ImageProcessingTemplate;
import com.fachada.cagepa.fachadacagepa.domain.util.ImageDigitExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorImageProcessor implements HidromeImageProcessor {
    @Autowired
    private ImageDigitExtractor extractor;

    @Override
    public String extractReading(String imagePath) throws Exception {
        ImageProcessingTemplate template = new ColaboradorSixDigitImageProcessor();
        return template.processImage(imagePath);
    }

    @Override
    public boolean supports(String imagePath) throws Exception {
        try {
            ImageProcessingTemplate template = new ColaboradorSixDigitImageProcessor();
            String value = template.processImage(imagePath);
            System.out.println(value);
            return value.matches("\\d{6}");
        } catch (Exception e) {
            return false;
        }
    }
}
