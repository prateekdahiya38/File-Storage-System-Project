package com.example.filesystem.exceptions;

public class FileDoesNotExistException extends Exception{
    public FileDoesNotExistException(String message) {
        super(message);
    }
}
