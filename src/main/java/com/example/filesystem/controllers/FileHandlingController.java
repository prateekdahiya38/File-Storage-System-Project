package com.example.filesystem.controllers;

import com.example.filesystem.dtos.requestDtos.FileDownloadRequestDto;
import com.example.filesystem.dtos.requestDtos.GetFileWithAllVersionRequestDto;
import com.example.filesystem.dtos.requestDtos.SharableLinkRequestDto;
import com.example.filesystem.dtos.responseDtos.FileResponseDto;
import com.example.filesystem.dtos.responseDtos.FileUploadResponseDto;
import com.example.filesystem.exceptions.*;
import com.example.filesystem.models.File;
import com.example.filesystem.services.FileHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileHandlingController {
    @Autowired
    private FileHandlingService fileHandlingService;

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponseDto> uploadFile(@RequestParam ("file") MultipartFile file, @RequestHeader ("AUTH_TOKEN") String token) throws IOException, FileIsEmptyException, UserNotFoundException, InvalidSessionException, SessionExpiredException {
        return ResponseEntity.ok(fileHandlingService.uploadFile(file,token));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestBody FileDownloadRequestDto request,@RequestHeader ("AUTH_TOKEN") String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException {
        File file = fileHandlingService.downloadFile(request,token);
        return ResponseEntity.ok().contentType(MediaType.valueOf(file.getType())).body(file.getData());
    }

    @GetMapping("/{fileName}/{version}")
    public ResponseEntity<byte[]> shareFile(@PathVariable("fileName") String fileName,@PathVariable ("version") int version)throws FileDoesNotExistException{
        File file = fileHandlingService.shareFile(fileName,version);
        return ResponseEntity.ok().contentType(MediaType.valueOf(file.getType())).body(file.getData());
    }

    @GetMapping("/share")
    public ResponseEntity<String> generateSharableLink(@RequestBody SharableLinkRequestDto request, @RequestHeader ("AUTH_TOKEN") String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException {
        return ResponseEntity.ok(fileHandlingService.generateSharableLink(request,token));
    }



    @GetMapping("/versions")
    public ResponseEntity<List<FileResponseDto>> getFileWithAllVersion(@RequestBody GetFileWithAllVersionRequestDto request,@RequestHeader ("AUTH_TOKEN") String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException {
        return  ResponseEntity.ok(fileHandlingService.getFileWithAllVersion(request,token));
    }
}
