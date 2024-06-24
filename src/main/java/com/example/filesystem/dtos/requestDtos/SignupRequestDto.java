package com.example.filesystem.dtos.requestDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDto {
    private String userName;
    private String email;
    private String password;
}
