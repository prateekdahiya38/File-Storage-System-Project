package com.example.filesystem.exceptions;

public class UnauthorizedRequestException extends Exception{
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}
