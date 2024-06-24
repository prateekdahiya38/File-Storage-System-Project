package com.example.filesystem.services;

import com.example.filesystem.dtos.requestDtos.LoginRequestDto;
import com.example.filesystem.dtos.requestDtos.SignupRequestDto;
import com.example.filesystem.dtos.responseDtos.UserDetailDto;
import com.example.filesystem.dtos.responseDtos.ValidationResponseDto;
import com.example.filesystem.exceptions.UnauthorizedRequestException;
import com.example.filesystem.exceptions.UserAlreadyExistException;
import com.example.filesystem.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;


public interface AuthService {
    UserDetailDto signup(SignupRequestDto request) throws UserAlreadyExistException;
    ResponseEntity<UserDetailDto> login(LoginRequestDto request) throws UserNotFoundException, UnauthorizedRequestException;
    ValidationResponseDto validateToken(String token);
    void logout(String token);
}
