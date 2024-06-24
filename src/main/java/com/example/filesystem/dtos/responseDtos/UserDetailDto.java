package com.example.filesystem.dtos.responseDtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDetailDto {
    private UUID userId;
    private String name;
    private String email;
    private List<RoleResponseDto> roles;
}
