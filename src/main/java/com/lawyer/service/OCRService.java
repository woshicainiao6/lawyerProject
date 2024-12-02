package com.lawyer.service;

import com.lawyer.pojo.User;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface OCRService {

    User getUserInfo(MultipartFile file) throws InterruptedException, TesseractException, IOException;
}
