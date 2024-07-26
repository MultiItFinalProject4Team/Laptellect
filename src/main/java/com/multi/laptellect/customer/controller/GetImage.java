package com.multi.laptellect.customer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;

@Controller
@RequiredArgsConstructor
public class GetImage {
    //이미지 출력
    @GetMapping("/images/{image}")
    public ResponseEntity<Resource> downloadImage(@PathVariable("image") String fileName)  {
        String directory = System.getProperty("user.dir") + "/uploads/";
        String filePath = directory + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        Resource resource = new FileSystemResource(file); // 파일을 리소스로 변환


        // 파일 확장자를 통해 MIME 타입을 결정
        String mimeType;
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        switch (ext) {
            case "jpg":
            case "jpeg":
                mimeType = "image/jpeg";
                break;
            case "png":
                mimeType = "image/png";
                break;
            case "gif":
                mimeType = "image/gif";
                break;
            default:
                mimeType = "application/octet-stream";  // 일반적인 바이너리 파일
        }

        // Content-Type 헤더를 설정하고 파일을 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
