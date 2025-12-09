package com.fachada.cagepa.fachadacagepa.domain.enterprise.template;

import com.fachada.cagepa.fachadacagepa.domain.util.ImageDigitExtractor;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.IOException;

import static org.bytedeco.opencv.global.opencv_imgcodecs.imread;

public abstract class ImageProcessingTemplate {

    protected final ImageDigitExtractor extractor;

    {
        try {
            extractor = new ImageDigitExtractor();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final String processImage(String imagePath) throws Exception {
        Mat image = loadImage(imagePath);
        validateImage(image);
        Mat processedRegion = extractDigitRegion(image);
        String rawResult = performOCR(processedRegion);
        return formatResult(rawResult);
    }

    protected Mat loadImage(String path) throws IOException {
        Mat mat = imread(path);
        if (mat.empty()) {
            throw new IOException("Imagem não carregada: " + path);
        }
        return mat;
    }

    protected abstract void validateImage(Mat image);
    protected abstract Mat extractDigitRegion(Mat image);
    protected abstract String performOCR(Mat region) throws Exception;
    protected abstract String formatResult(String rawResult);
}
