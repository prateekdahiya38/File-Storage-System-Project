package com.example.filesystem.services;

import com.example.filesystem.dtos.requestDtos.LoginRequestDto;
import com.example.filesystem.dtos.requestDtos.SignupRequestDto;
import com.example.filesystem.dtos.responseDtos.ValidationResponseDto;
import com.example.filesystem.exceptions.UnauthorizedRequestException;
import com.example.filesystem.exceptions.UserAlreadyExistException;
import com.example.filesystem.exceptions.UserNotFoundException;
import com.example.filesystem.models.Session;
import com.example.filesystem.models.SessionStatus;
import com.example.filesystem.models.User;
import com.example.filesystem.repositories.RoleRepository;
import com.example.filesystem.repositories.SessionRepository;
import com.example.filesystem.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSignup_UserAlreadyExist() {
        SignupRequestDto request = new SignupRequestDto();
        request.setEmail("test@example.com");
        request.setUserName("testUser");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> authService.signup(request));
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }


    private void assertNotNull(UUID userId) {
    }

    @Test
    public void testLogin_UserNotFound() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(request));
        verify(userRepository, times(1)).findByEmail(request.getEmail());
    }

    @Test
    public void testLogin_UnauthorizedRequest() {
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("test@example.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(UnauthorizedRequestException.class, () -> authService.login(request));
    }


    @Test
    public void testValidateToken_SessionInvalid() {
        String token = "invalidToken";

        when(sessionRepository.findByToken(token)).thenReturn(Optional.empty());

        ValidationResponseDto response = authService.validateToken(token);

        assertEquals(SessionStatus.INVALID, response.getSessionStatus());
    }

    @Test
    public void testValidateToken_SessionExpired() {
        String token = "expiredToken";

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setExpiringAt(Instant.now().minusSeconds(3600));

        when(sessionRepository.findByToken(token)).thenReturn(Optional.of(session));

        ValidationResponseDto response = authService.validateToken(token);

        assertEquals(SessionStatus.EXPIRED, response.getSessionStatus());
    }

    @Test
    public void testValidateToken_Success() {
        String token = "validToken";

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setExpiringAt(Instant.now().plusSeconds(3600));

        when(sessionRepository.findByToken(token)).thenReturn(Optional.of(session));
        when(jwtService.extractEmail(token)).thenReturn("test@example.com");

        ValidationResponseDto response = authService.validateToken(token);

        assertEquals(SessionStatus.ACTIVE, response.getSessionStatus());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    public void testLogout() {
        String token = "validToken";

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);

        when(sessionRepository.findByToken(token)).thenReturn(Optional.of(session));

        authService.logout(token);

        verify(sessionRepository, times(1)).save(session);
        assertEquals(SessionStatus.LOGGED_OUT, session.getSessionStatus());
    }
}
