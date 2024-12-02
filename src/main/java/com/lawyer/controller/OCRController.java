package com.lawyer.controller;

import com.lawyer.pojo.Result;
import com.lawyer.pojo.User;
import com.lawyer.service.OCRService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import static org.opencv.highgui.HighGui.imshow;
import static org.opencv.highgui.HighGui.waitKey;

@Slf4j
@RestController
public class OCRController {
    @Autowired
    private OCRService ocrService;

    @PostMapping("/lawyer/OCR")
    public Result OCR(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }
        try {
            User user =ocrService.getUserInfo(file);
            return Result.success(user);
        } catch (IOException e) {
            return Result.error("文件读取失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("OCR 处理失败：" + e.getMessage());
        }
    }

    @GetMapping("/lawyer/test")
    public Result test(){
        return Result.success();
    }
}
