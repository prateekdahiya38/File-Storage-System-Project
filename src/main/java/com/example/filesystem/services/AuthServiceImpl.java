package com.example.filesystem.services;

import com.example.filesystem.dtos.requestDtos.LoginRequestDto;
import com.example.filesystem.dtos.requestDtos.SignupRequestDto;
import com.example.filesystem.dtos.responseDtos.RoleResponseDto;
import com.example.filesystem.dtos.responseDtos.UserDetailDto;
import com.example.filesystem.dtos.responseDtos.ValidationResponseDto;
import com.example.filesystem.exceptions.UnauthorizedRequestException;
import com.example.filesystem.exceptions.UserAlreadyExistException;
import com.example.filesystem.exceptions.UserNotFoundException;
import com.example.filesystem.models.Role;
import com.example.filesystem.models.Session;
import com.example.filesystem.models.SessionStatus;
import com.example.filesystem.models.User;
import com.example.filesystem.repositories.RoleRepository;
import com.example.filesystem.repositories.SessionRepository;
import com.example.filesystem.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMapAdapter;


import java.time.Instant;
import java.util.*;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private RoleRepository roleRepository;


    @Override
    public UserDetailDto signup(SignupRequestDto request) throws UserAlreadyExistException {
        UserDetailDto response = new UserDetailDto();
        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if(userOptional.isPresent()){
            throw new UserAlreadyExistException("User Already Exist");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getUserName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = new Role();
        role.setRole("USER");
        roleRepository.save(role);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        response.setEmail(savedUser.getEmail());
        response.setName(savedUser.getName());
        response.setUserId(savedUser.getId());
        setRoleInResponse(response, savedUser);
        return response;
    }



    @Override
    public ResponseEntity<UserDetailDto> login(LoginRequestDto request) throws UserNotFoundException, UnauthorizedRequestException {
        UserDetailDto response = new UserDetailDto();
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()-> new UserNotFoundException("User does not present"));
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new UnauthorizedRequestException("this is an unauthorized request, Please check your password");
        }

        String token = jwtService.generateToken(user);
        MultiValueMapAdapter<String,String> headers = new MultiValueMapAdapter<>(new HashMap<>());
        headers.add("AUTH_TOKEN", token);

        Session session = new Session();
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setToken(token);
        session.setUser(user);
        session.setExpiringAt(jwtService.extractExpiration(token).toInstant());
        sessionRepository.save(session);

        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        setRoleInResponse(response, user);
        return new ResponseEntity<>(response,headers, HttpStatus.OK);

    }


    @Override
    public ValidationResponseDto validateToken(String token) {
        ValidationResponseDto response = new ValidationResponseDto();
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isEmpty()){
           response.setSessionStatus(SessionStatus.INVALID);
           return response;
        }
        if (!sessionOptional.get().getSessionStatus().equals(SessionStatus.ACTIVE)){
            response.setSessionStatus(SessionStatus.EXPIRED);
            return response;
        }

        Instant currentTime = Instant.now();
        Instant sessionExpiryTime  = sessionOptional.get().getExpiringAt();
        if (sessionExpiryTime.isBefore(currentTime)){
            response.setSessionStatus(SessionStatus.EXPIRED);
            return response;
        }
        response.setSessionStatus(SessionStatus.ACTIVE);
        String email = jwtService.extractEmail(token);
        response.setEmail(email);
        return response;
    }


    @Override
    public void logout(String token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        Session session = sessionOptional.get();
        session.setSessionStatus(SessionStatus.LOGGED_OUT);
        sessionRepository.save(session);
    }



    private static void setRoleInResponse(UserDetailDto response, User user) {
        List<RoleResponseDto> roleResponses = new ArrayList<>();
        for (Role existingRole : user.getRoles() ) {
            RoleResponseDto rolesResponse = new RoleResponseDto();
            rolesResponse.setRole(existingRole.getRole());
            roleResponses.add(rolesResponse);
        }
        response.setRoles(roleResponses);
    }
}
