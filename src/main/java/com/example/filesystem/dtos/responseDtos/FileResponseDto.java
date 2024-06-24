package com.example.filesystem.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileResponseDto {
    private String fileName;
    private int version;
}
