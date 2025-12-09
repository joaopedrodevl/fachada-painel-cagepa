package com.fachada.cagepa.fachadacagepa.domain.enterprise.template;

import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;

import java.awt.image.BufferedImage;

import static org.bytedeco.opencv.global.opencv_imgproc.*;

public class SixDigitImageProcessor extends ImageProcessingTemplate {
    @Override
    protected Mat extractDigitRegion(Mat image) {
        Mat gray = new Mat();
        cvtColor(image, gray, COLOR_BGR2GRAY);
        Mat binary = new Mat();
        threshold(gray, binary, 128, 255, THRESH_BINARY_INV);

        Rect roi = new Rect(340, 240, 240, 60);
        return binary.apply(roi);
    }

    @Override
    protected String performOCR(Mat region) throws Exception {
        BufferedImage img = convertToBufferedImage(region);
        return extractor.extractDigitsFromImage(img);
    }

    @Override
    protected String formatResult(String rawResult) {
        if (!rawResult.matches("\\d{6}")) {
            throw new IllegalArgumentException("Formato inválido para SHA A");
        }
        return rawResult;
    }

    @Override
    protected void validateImage(Mat image) {
        if (image.rows() < 100 || image.cols() < 100) {
            throw new IllegalArgumentException("Imagem muito pequena");
        }
    }

    private BufferedImage convertToBufferedImage(Mat mat) {
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        Java2DFrameConverter java2DConverter = new Java2DFrameConverter();
        org.bytedeco.javacv.Frame frame = converter.convert(mat);
        return java2DConverter.getBufferedImage(frame);
    }
}
