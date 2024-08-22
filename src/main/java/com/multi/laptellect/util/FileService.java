package com.multi.laptellect.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.multi.laptellect.common.model.FileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * 오브젝트 스토리지 파일 업로드 유틸클래스
 *
 * @author 안진원
 */
@Component
@RequiredArgsConstructor
public class FileService {  
  
    private final AmazonS3Client amazonS3Client;
  
    @Value("${spring.ncp.s3.bucket}")
    private String bucketName;
  
    public String getUuidFileName(String fileName) {  
        String ext = fileName.substring(fileName.indexOf(".") + 1);  
        return UUID.randomUUID().toString() + "." + ext;
    }

    /**
     * MultipartFile을 오브젝트 스토리지에 업로드하는 메소드
     *
     * @param multipartFile 파일
     * @param filePath 경로
     * @return 업로드한 파일의 정보를 담은 dto 반환
     */
    //NOTICE: filePath의 맨 앞에 /는 안붙여도됨. ex) image/customer
    public FileDto uploadFiles(MultipartFile multipartFile, String filePath) {
  
            FileDto s3files;
            String originalFileName = multipartFile.getOriginalFilename();  
            String uploadFileName = getUuidFileName(originalFileName);  
            String uploadFileUrl = "";  
  
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());  
            objectMetadata.setContentType(multipartFile.getContentType());  
  
            try (InputStream inputStream = multipartFile.getInputStream()) {
  
                String keyName = filePath + "/" + uploadFileName;  
  
                // S3에 폴더 및 파일 업로드  
                amazonS3Client.putObject(  
                        new PutObjectRequest(bucketName, keyName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));
  
                // S3에 업로드한 폴더 및 파일 URL  
                uploadFileUrl = "https://kr.object.ncloudstorage.com/"+ bucketName + "/" + keyName;  
  
            } catch (IOException e) {
                e.printStackTrace();  
            }  
  
            s3files = FileDto.builder()
                            .originalFileName(originalFileName)  
                            .uploadFileName(uploadFileName)  
                            .uploadFilePath(filePath)  
                            .uploadFileUrl(uploadFileUrl)  
                            .build();
        return s3files;  
    }

    /**
     * 오브젝트 스토리지 파일 삭제 메소드
     *
     * @param url 파일경로를 포함한 파일명
     * @return 성공시 1을 반환 예외발생 시 0을 반환
     */
    public int deleteFile(String url){
        try {
            try {
                String prefix = "4team/";
                int prefixIndex = url.indexOf(prefix);

                if (prefixIndex != -1) {
                    url = url.substring(prefixIndex + prefix.length());
                    amazonS3Client.deleteObject(bucketName, url);
                } else {
                    System.out.println("주어진 URL에 '4team/'이 포함되지 않았습니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            return 0;
        }
        return 1;
    }



}