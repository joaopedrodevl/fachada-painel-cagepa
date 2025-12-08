package com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy;

import com.fachada.cagepa.fachadacagepa.domain.util.ImageDigitExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ColaboradorImageProcessor implements HidromeImageProcessor {
    @Autowired
    private ImageDigitExtractor extractor;

    @Override
    public String extractReading(String imagePath) throws Exception {
        return extractor.extractDigits(imagePath);
    }
}
