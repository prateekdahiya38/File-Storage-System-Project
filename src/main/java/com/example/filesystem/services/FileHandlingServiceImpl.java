package com.example.filesystem.services;

import com.example.filesystem.dtos.requestDtos.FileDownloadRequestDto;
import com.example.filesystem.dtos.requestDtos.GetFileWithAllVersionRequestDto;
import com.example.filesystem.dtos.requestDtos.SharableLinkRequestDto;
import com.example.filesystem.dtos.responseDtos.FileResponseDto;
import com.example.filesystem.dtos.responseDtos.FileUploadResponseDto;
import com.example.filesystem.dtos.responseDtos.ValidationResponseDto;
import com.example.filesystem.exceptions.*;
import com.example.filesystem.mappers.FileMapper;
import com.example.filesystem.models.File;
import com.example.filesystem.models.SessionStatus;
import com.example.filesystem.models.User;
import com.example.filesystem.repositories.FileStorageRepository;
import com.example.filesystem.repositories.SessionRepository;
import com.example.filesystem.repositories.UserRepository;
import com.example.filesystem.utils.FileUtils;
import com.example.filesystem.versions.FileVersioning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
public class FileHandlingServiceImpl implements FileHandlingService{
    @Autowired
    private FileStorageRepository fileStorageRepository;
    @Autowired
    private FileVersioning fileVersioning;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public FileUploadResponseDto uploadFile(MultipartFile multipartFile, String token) throws FileIsEmptyException, IOException, UserNotFoundException, InvalidSessionException, SessionExpiredException {
        FileUploadResponseDto response = new FileUploadResponseDto();

        User user = validation(token);

        if (multipartFile == null){
            throw new FileIsEmptyException("file is empty");
        }
        File file = FileMapper.multiPartFileToFileConversion(multipartFile);
        file.setUser(user);
        File savedFile = fileVersioning.saveVersion(file);
        response.setFileName(savedFile.getName());
        response.setFileVersion(savedFile.getVersion());
        response.setMessage("file is successfully uploaded");
        return response;
    }

    @Override
    public File downloadFile(FileDownloadRequestDto request, String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException {

        User user = validation(token);

        File file = fileStorageRepository.findByNameAndVersion(request.getFileName(), request.getVersion()).orElseThrow(()->new FileDoesNotExistException("file Does not exist"));
        if (user.getId() != file.getUser().getId()){
            throw new UnauthorizedRequestException("UnAuthorized Access");
        }
        byte[] extractedFile = FileUtils.decompressFile(file.getData());
        file.setData(extractedFile);
        return file;
    }


    @Override
    public File shareFile(String fileName,int version) throws FileDoesNotExistException{
        File file = fileStorageRepository.findByNameAndVersion(fileName,version).orElseThrow(()->new FileDoesNotExistException("file Does not exist"));
        byte[] extractedFile = FileUtils.decompressFile(file.getData());
        file.setData(extractedFile);
        return file;
    }

    @Override
    public String generateSharableLink(SharableLinkRequestDto request, String token) throws UserNotFoundException, InvalidSessionException, SessionExpiredException, UnauthorizedRequestException, FileDoesNotExistException {
        User user = validation(token);
        File file = fileStorageRepository.findByNameAndVersion(request.getFileName(), request.getVersion()).orElseThrow(()->new FileDoesNotExistException("file Does not exist"));
        if (user.getId() != file.getUser().getId()){
            throw new UnauthorizedRequestException("UnAuthorized Access");
        }
        return ("http://localhost:8080/files/" + request.getFileName()+"/"+request.getVersion());
    }


    @Override
    public List<FileResponseDto> getFileWithAllVersion(GetFileWithAllVersionRequestDto request, String token) throws FileDoesNotExistException, UserNotFoundException, UnauthorizedRequestException, InvalidSessionException, SessionExpiredException {
        List<FileResponseDto> responses = new ArrayList<>();

        User user = validation(token);

        List<File> existingFiles = fileStorageRepository.findByNameOrderByVersionDesc(request.getFileName());
        if (existingFiles.isEmpty()){
            throw new FileDoesNotExistException("File does not exist");
        }
        if (user.getId() != existingFiles.get(0).getUser().getId()){
            throw new UnauthorizedRequestException("UnAuthorized Access");
        }
        for (File file : existingFiles){
            FileResponseDto response = FileMapper.fileToFileResponseDtoConversion(file);
            responses.add(response);
        }
        return responses;
    }


    private User validation(String token) throws UserNotFoundException, InvalidSessionException, SessionExpiredException {
        ValidationResponseDto validateToken = authService.validateToken(token);
        if (validateToken.getSessionStatus().equals(SessionStatus.INVALID)){
            throw new InvalidSessionException("Session Invalid");
        }
        if (!validateToken.getSessionStatus().equals(SessionStatus.ACTIVE)){
            throw new SessionExpiredException("Session has Expired");
        }
        User user = userRepository.findByEmail(validateToken.getEmail()).orElseThrow(() ->new UserNotFoundException("User Does Not Found"));
        return user;
    }

}
