package com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy;

import com.fachada.cagepa.fachadacagepa.domain.util.ImageDigitExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProprietarioImageProcessor implements HidromeImageProcessor{

    @Autowired
    private ImageDigitExtractor imageDigitExtractor;

    @Override
    public String extractReading(String imagePath) throws Exception{
        return imageDigitExtractor.extractDigits(imagePath);
    }
}
