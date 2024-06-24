package com.example.filesystem.dtos.requestDtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FileDownloadRequestDto {
    private String fileName;
    private int version;
}
