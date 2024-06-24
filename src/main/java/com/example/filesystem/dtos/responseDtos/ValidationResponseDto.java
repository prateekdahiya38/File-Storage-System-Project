package com.example.filesystem.dtos.responseDtos;

import com.example.filesystem.models.SessionStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ValidationResponseDto {
    private SessionStatus sessionStatus;
    private String email;
}
