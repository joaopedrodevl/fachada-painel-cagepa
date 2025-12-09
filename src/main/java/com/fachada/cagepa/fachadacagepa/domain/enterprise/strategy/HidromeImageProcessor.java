package com.fachada.cagepa.fachadacagepa.domain.enterprise.strategy;


public interface HidromeImageProcessor {
    String extractReading(String imagePath) throws Exception;
    boolean supports(String imagePath) throws Exception;
}
