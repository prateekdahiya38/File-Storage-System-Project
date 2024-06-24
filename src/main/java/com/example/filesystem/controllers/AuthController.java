package com.example.filesystem.controllers;


import com.example.filesystem.dtos.requestDtos.LoginRequestDto;
import com.example.filesystem.dtos.requestDtos.SignupRequestDto;
import com.example.filesystem.dtos.responseDtos.UserDetailDto;
import com.example.filesystem.dtos.responseDtos.ValidationResponseDto;
import com.example.filesystem.exceptions.UnauthorizedRequestException;
import com.example.filesystem.exceptions.UserAlreadyExistException;
import com.example.filesystem.exceptions.UserNotFoundException;
import com.example.filesystem.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDetailDto> signUp(@RequestBody SignupRequestDto request) throws UserAlreadyExistException, UserAlreadyExistException {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserDetailDto> login(@RequestBody LoginRequestDto request) throws UserNotFoundException, UnauthorizedRequestException {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader ("AUTH_TOKEN") String token){
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidationResponseDto> validate(@RequestHeader ("AUTH_TOKEN") String token){
        return ResponseEntity.ok(authService.validateToken(token));
    }
}
