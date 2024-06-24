package com.example.filesystem.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadResponseDto {
    private String message;
    private String fileName;
    private int fileVersion;
}
