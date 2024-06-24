package com.example.filesystem.services;

import com.example.filesystem.dtos.requestDtos.FileDownloadRequestDto;
import com.example.filesystem.dtos.requestDtos.SharableLinkRequestDto;
import com.example.filesystem.dtos.responseDtos.FileUploadResponseDto;
import com.example.filesystem.dtos.responseDtos.ValidationResponseDto;
import com.example.filesystem.exceptions.*;
import com.example.filesystem.models.File;
import com.example.filesystem.models.SessionStatus;
import com.example.filesystem.models.User;
import com.example.filesystem.repositories.FileStorageRepository;
import com.example.filesystem.repositories.UserRepository;
import com.example.filesystem.versions.FileVersioning;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FileHandlingServiceImplTest {

    @InjectMocks
    private FileHandlingServiceImpl fileHandlingService;

    @Mock
    private FileStorageRepository fileStorageRepository;

    @Mock
    private FileVersioning fileVersioning;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testUploadFile_FileIsEmpty() {
        String token = "validToken";

        when(authService.validateToken(token)).thenReturn(getValidValidationResponseDto());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(getTestUser()));

        assertThrows(FileIsEmptyException.class, () -> fileHandlingService.uploadFile(null, token));
    }

    @Test
    public void testUploadFile_Success() throws Exception {
        String token = "validToken";
        byte[] fileData = "test data".getBytes();
        File file = new File();
        file.setName("test.txt");
        file.setVersion(1);

        when(authService.validateToken(token)).thenReturn(getValidValidationResponseDto());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(getTestUser()));
        when(multipartFile.getBytes()).thenReturn(fileData);
        when(fileVersioning.saveVersion(any(File.class))).thenReturn(file);

        FileUploadResponseDto response = fileHandlingService.uploadFile(multipartFile, token);

        assertNotNull(response);
        assertEquals("test.txt", response.getFileName());
        assertEquals(1, response.getFileVersion());
        assertEquals("file is successfully uploaded", response.getMessage());
    }

    @Test
    public void testDownloadFile_FileDoesNotExist() {
        String token = "validToken";
        FileDownloadRequestDto request = new FileDownloadRequestDto();
        request.setFileName("test.txt");
        request.setVersion(1);

        when(authService.validateToken(token)).thenReturn(getValidValidationResponseDto());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(getTestUser()));
        when(fileStorageRepository.findByNameAndVersion(anyString(), anyInt())).thenReturn(Optional.empty());

        assertThrows(FileDoesNotExistException.class, () -> fileHandlingService.downloadFile(request, token));
    }

    @Test
    public void testDownloadFile_UnauthorizedRequest() {
        String token = "validToken";
        FileDownloadRequestDto request = new FileDownloadRequestDto();
        request.setFileName("test.txt");
        request.setVersion(1);

        User anotherUser = getTestUser();
        anotherUser.setId(UUID.randomUUID());

        File file = new File();
        file.setName("test.txt");
        file.setVersion(1);
        file.setUser(anotherUser);

        when(authService.validateToken(token)).thenReturn(getValidValidationResponseDto());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(getTestUser()));
        when(fileStorageRepository.findByNameAndVersion(anyString(), anyInt())).thenReturn(Optional.of(file));

        assertThrows(UnauthorizedRequestException.class, () -> fileHandlingService.downloadFile(request, token));
    }


    @Test
    public void testShareFile_FileDoesNotExist() {
        when(fileStorageRepository.findByNameAndVersion(anyString(), anyInt())).thenReturn(Optional.empty());

        assertThrows(FileDoesNotExistException.class, () -> fileHandlingService.shareFile("test.txt", 1));
    }


    @Test
    public void testGenerateSharableLink_FileDoesNotExist() {
        String token = "validToken";
        SharableLinkRequestDto request = new SharableLinkRequestDto();
        request.setFileName("test.txt");
        request.setVersion(1);

        when(authService.validateToken(token)).thenReturn(getValidValidationResponseDto());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(getTestUser()));
        when(fileStorageRepository.findByNameAndVersion(anyString(), anyInt())).thenReturn(Optional.empty());

        assertThrows(FileDoesNotExistException.class, () -> fileHandlingService.generateSharableLink(request, token));
    }

    @Test
    public void testGenerateSharableLink_UnauthorizedRequest() {
        String token = "validToken";
        SharableLinkRequestDto request = new SharableLinkRequestDto();
        request.setFileName("test.txt");
        request.setVersion(1);

        User anotherUser = getTestUser();
        anotherUser.setId(UUID.randomUUID());

        File file = new File();
        file.setName("test.txt");
        file.setVersion(1);
        file.setUser(anotherUser);

        when(authService.validateToken(token)).thenReturn(getValidValidationResponseDto());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(getTestUser()));
        when(fileStorageRepository.findByNameAndVersion(anyString(), anyInt())).thenReturn(Optional.of(file));

        assertThrows(UnauthorizedRequestException.class, () -> fileHandlingService.generateSharableLink(request, token));
    }


    private ValidationResponseDto getValidValidationResponseDto() {
        ValidationResponseDto responseDto = new ValidationResponseDto();
        responseDto.setEmail("test@example.com");
        responseDto.setSessionStatus(SessionStatus.ACTIVE);
        return responseDto;
    }

    private User getTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("password");
        return user;
    }
}