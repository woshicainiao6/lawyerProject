package com.lawyer.util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.lawyer.util.ProcessImgUtil.matToBufferedImage;

public class OCRUtil {

    public static String performOCR(Mat img) throws TesseractException, IOException, InterruptedException {
        // 处理图像为 BufferedImage
        BufferedImage bufferedImage = matToBufferedImage(img);

        // 初始化Tesseract OCR实例
        ITesseract instance = new Tesseract();
        instance.setDatapath("D:\\Develop\\Java\\JavaProject\\lawyerProject\\src\\main\\resources\\testdata");  // 设置Tesseract的语言数据路径
        // 设置OCR语言：简体中文
        instance.setLanguage("chi_sim_fast");
        // 执行OCR识别
        String ocrResult = instance.doOCR(bufferedImage);
        return ocrResult;
    }

}
