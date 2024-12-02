package com.lawyer.service.impl;

import com.lawyer.pojo.Image;
import com.lawyer.pojo.User;
import com.lawyer.service.OCRService;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.lawyer.util.FormatResultUtil.extractInfo;
import static com.lawyer.util.OCRUtil.performOCR;
import static com.lawyer.util.ProcessImgUtil.*;
import static com.lawyer.util.RotateImageUtil.*;

/**
 * 保险服务的实现类
 */
@Service
public class OCRServiceImpl implements OCRService {
    static {
        try {
            URL url = ClassLoader.getSystemResource("opencv/opencv_java4100.dll");
            System.load(url.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUserInfo(MultipartFile file) throws InterruptedException, TesseractException, IOException {
        Mat originalImage = loadImage(file);
        // 处理图片的纵横比
        originalImage=comHorizontalRatio(originalImage);
        // 再次旋转确保图像水平放置
        originalImage = horizontalImage(originalImage);

        // 调整图像大小
        Mat resizeImage = resizeImg(originalImage, 800);

        // 获取原图像的所有边缘轮廓（为后续的图像旋转做准备）
        List<MatOfPoint> ImgEdgePoints = getOriginalImgEdge(resizeImage);

        // 获取原始图像最大的轮廓（为了查找出身份证的位置，从而进行图像旋转）
        MatOfPoint largestContour = getMaximumContour(resizeImage, ImgEdgePoints);

        // 执行图像旋转
        Image rotateImage = rotateImage(resizeImage, largestContour);

        // 执行原图像裁剪（如果有需要）
        Mat croppingImg = croppingImg(rotateImage.getRotatedImage(), rotateImage.getRotatedContours());

        // 获取裁切图像的所有边缘轮廓
        List<Rect> croppingImgEdgePoints = getAllContours(croppingImg);

        // 对裁剪后的图继续进行裁剪和拼接
        Mat montageNewImage = cuttingSplicingImg(croppingImg, croppingImgEdgePoints);

        // 处理拼接后的图像得到二值化的图像清晰度影响就在此处
        Mat processRresult = processCroppedImage(montageNewImage);

        // 设置拼接后的图像大小
        processRresult = resizeImg(processRresult, 500);

        // 将图像重新进行放缩，得到一个边缘比拼接后的图像大1.5倍的图像，增加识别准确率
        processRresult = regenerateImg(processRresult, 1.5);

        // 进行ORC识别
        String result = performOCR(processRresult);

        User user = new User();
        // 提取信息
        extractInfo(result, user);
        return user;
    }

}
