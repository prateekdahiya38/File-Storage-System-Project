package com.example.filesystem.exceptions.controlleradvicer;

import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class ExceptionResponseDto {
    private ResponseStatus responseStatus;
    private String message;
}
