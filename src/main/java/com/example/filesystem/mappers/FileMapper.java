package com.example.filesystem.mappers;

import com.example.filesystem.dtos.responseDtos.FileResponseDto;
import com.example.filesystem.models.File;
import com.example.filesystem.utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;

public class FileMapper {

    public static File multiPartFileToFileConversion(MultipartFile multipartFile) throws IOException {
        File file = new File();
        file.setName(multipartFile.getOriginalFilename());
        file.setType(multipartFile.getContentType());
        file.setData(FileUtils.compressFile(multipartFile.getBytes()));
        file.setUploadedAt(Instant.now());
        return file;
    }

    public static FileResponseDto fileToFileResponseDtoConversion(File file){
        FileResponseDto response = new FileResponseDto();
        response.setFileName(file.getName());
        response.setVersion(file.getVersion());
        return response;
    }
}
