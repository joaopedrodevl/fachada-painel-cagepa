package com.fachada.cagepa.fachadacagepa.domain.util;

import com.fachada.cagepa.fachadacagepa.config.SystemConfiguration;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.bytedeco.javacv.Frame;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.core.Core;
import org.opencv.imgproc.Imgproc;
import org.springframework.stereotype.Component;
import static org.bytedeco.opencv.global.opencv_core.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Logger;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

@Component
public class ImageDigitExtractor {

    private static final Logger logger = Logger.getLogger(ImageDigitExtractor.class.getName());

    private final String TESSDATA_PATH = SystemConfiguration.getInstance().getTessDataPath();

    private final ITesseract tesseract;

    public ImageDigitExtractor() throws IOException {
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath(TESSDATA_PATH);
        this.tesseract.setLanguage("por");
        this.tesseract.setOcrEngineMode(1); // LSTM
        this.tesseract.setPageSegMode(7);   // Single text line
    }

    public String extractDigitsWithROI(String imagePath, int x, int y, int width, int height) throws IOException, TesseractException {
        Mat src = opencv_imgcodecs.imread(imagePath);
        if (src.empty()) {
            throw new IOException("Imagem não carregou: " + imagePath);
        }

        Mat gray = new Mat();
        cvtColor(src, gray, COLOR_BGR2GRAY);

        Mat binary = new Mat();
        threshold(gray, binary, 128, 255, THRESH_BINARY_INV);

        Rect roi = new Rect(x, y, width, height);
        Mat digitRegion = binary.apply(roi);

//        opencv_imgcodecs.imwrite("/tmp/debug_digit_region_" + x + "_" + y + ".png", digitRegion);

        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter java2DConverter = new Java2DFrameConverter();
        Frame frame = converter.convert(digitRegion);
        BufferedImage bufferedImage = java2DConverter.getBufferedImage(frame);

        String result = tesseract.doOCR(bufferedImage).trim();
        result = result.replaceAll("[^0-9]", "").trim();

        if (result.length() != 6) {
            throw new IllegalArgumentException("Não foram extraídos 6 dígitos.");
        }

        return result;
    }

    public String extractDigitsFromImage(BufferedImage img) {
        try {
            String result = tesseract.doOCR(img).trim();
            result = result.replaceAll("[^0-9]", ""); // manter só dígitos

            if (result.length() != 6) {
                throw new IllegalArgumentException("Falha ao extrair 6 dígitos válidos.");
            }

            logger.info("Dígitos extraídos: " + result);
            return result;
        } catch (TesseractException e) {
            logger.severe("Erro durante OCR: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}