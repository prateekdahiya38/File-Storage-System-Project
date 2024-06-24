package com.example.filesystem.services;

import com.example.filesystem.dtos.requestDtos.FileDownloadRequestDto;
import com.example.filesystem.dtos.requestDtos.GetFileWithAllVersionRequestDto;
import com.example.filesystem.dtos.requestDtos.SharableLinkRequestDto;
import com.example.filesystem.dtos.responseDtos.FileResponseDto;
import com.example.filesystem.dtos.responseDtos.FileUploadResponseDto;
import com.example.filesystem.exceptions.*;
import com.example.filesystem.models.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileHandlingService {
    FileUploadResponseDto uploadFile(MultipartFile multipartFile, String token) throws IOException, FileIsEmptyException, UserNotFoundException, InvalidSessionException, SessionExpiredException;
    File downloadFile(FileDownloadRequestDto request, String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException;
    File shareFile(String fileName, int version) throws FileDoesNotExistException;
    String generateSharableLink(SharableLinkRequestDto request,String token) throws UserNotFoundException, InvalidSessionException, SessionExpiredException, UnauthorizedRequestException, FileDoesNotExistException;
    List<FileResponseDto> getFileWithAllVersion(GetFileWithAllVersionRequestDto request, String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException;
}
