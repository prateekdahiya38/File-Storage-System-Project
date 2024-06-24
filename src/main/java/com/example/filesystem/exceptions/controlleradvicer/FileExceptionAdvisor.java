package com.example.filesystem.exceptions.controlleradvicer;

import com.example.filesystem.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FileExceptionAdvisor {
    @ExceptionHandler(FileIsEmptyException.class)
    public ResponseEntity<ExceptionResponseDto> fileIsEmptyException(FileIsEmptyException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(FileDoesNotExistException.class)
    public ResponseEntity<ExceptionResponseDto> fileDoesntExistException(FileDoesNotExistException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ExceptionResponseDto> userAlreadyPresentException(UserAlreadyExistException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> userDoesNotFoundException(UserNotFoundException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedRequestException.class)
    public ResponseEntity<ExceptionResponseDto> unAuthorizedException(UnauthorizedRequestException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidSessionException.class)
    public ResponseEntity<ExceptionResponseDto> invalidSessionException(InvalidSessionException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ExceptionResponseDto> sessionExpiredException(SessionExpiredException e){
        ExceptionResponseDto response = new ExceptionResponseDto();
        response.setResponseStatus(ResponseStatus.FAILURE);
        response.setMessage(e.getMessage());
        return new ResponseEntity<>(response,HttpStatus.NOT_FOUND);
    }
}
