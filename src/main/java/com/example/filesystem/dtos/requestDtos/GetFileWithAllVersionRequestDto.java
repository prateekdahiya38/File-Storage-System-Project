package com.example.filesystem.dtos.requestDtos;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GetFileWithAllVersionRequestDto {
    private String fileName;
}
