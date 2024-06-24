package com.example.filesystem.dtos.requestDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SharableLinkRequestDto {
    private String fileName;
    private int version;
}
