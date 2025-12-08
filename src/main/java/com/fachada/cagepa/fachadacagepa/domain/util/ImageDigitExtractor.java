package com.fachada.cagepa.fachadacagepa.domain.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Component
public class ImageDigitExtractor {

    private static final Logger logger = Logger.getLogger(ImageDigitExtractor.class.getName());

    // Caminho do Tesseract (ajuste conforme sua versão do Ubuntu)
    private static final String TESSDATA_PATH = "/usr/share/tesseract-ocr/4.00/tessdata";

    private final ITesseract tesseract;

    public ImageDigitExtractor() {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(TESSDATA_PATH);
        this.tesseract.setLanguage("por");
        this.tesseract.setOcrEngineMode(1); // LSTM
        this.tesseract.setPageSegMode(7);   // Single text line
    }

    /**
     * Extrai os 6 dígitos do hidrômetro.
     * @param imagePath Caminho absoluto da imagem
     * @return String com 6 dígitos (ex: "000009")
     * @throws IOException se a imagem não for carregada
     * @throws TesseractException se OCR falhar
     */
    public String extractDigits(String imagePath) throws IOException, TesseractException {
        logger.info("Processando imagem: " + imagePath);

        // 1. Carregar imagem
        Mat src = opencv_imgcodecs.imread(imagePath);
        if (src.empty()) {
            throw new IOException("Falha ao carregar imagem: " + imagePath);
        }

        // 2. Converter para escala de cinza
        Mat gray = new Mat();
        cvtColor(src, gray, COLOR_BGR2GRAY);

        // 3. Binarizar (ajuste o threshold conforme o contraste da imagem)
        Mat binary = new Mat();
        Mat equalized = new Mat();
        equalizeHist(gray, equalized);
        threshold(equalized, binary, 128, 255, THRESH_BINARY_INV);
        threshold(gray, binary, 128, 255, THRESH_BINARY_INV);

        // 4. Recortar região dos dígitos (AJUSTE ESTAS COORDENADAS!)
        Rect roi = new Rect(340, 270, 240, 60); // ← ← ← AJUSTE AQUI!
        Mat digitRegion = binary.apply(roi);

        opencv_imgcodecs.imwrite("/tmp/debug_digit_region.png", digitRegion);
        logger.info("Região recortada salva em: /tmp/debug_digit_region.png");

        // 5. Converter Mat → BufferedImage para Tesseract
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter java2DConverter = new Java2DFrameConverter();
        org.bytedeco.javacv.Frame frame = converter.convert(digitRegion);
        BufferedImage bufferedImage = java2DConverter.getBufferedImage(frame);

        // 6. OCR
        String result = tesseract.doOCR(bufferedImage).trim();
        result = result.replaceAll("[^0-9]", ""); // manter só dígitos

        // 7. Validar
        if (result.length() != 6) {
            logger.warning("OCR retornou valor inválido: '" + result + "'");
            throw new IllegalArgumentException("Falha ao extrair 6 dígitos válidos.");
        }

        logger.info("✅ Dígitos extraídos: " + result);
        return result;
    }
}