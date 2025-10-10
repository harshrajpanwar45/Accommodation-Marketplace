package com.dcl.accommodate.service.implementation;

import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.response.AuthResponse;
import com.dcl.accommodate.enums.UserRole;
import com.dcl.accommodate.exceptions.UserAlreadyExistByEmailException;
import com.dcl.accommodate.model.User;
import com.dcl.accommodate.repository.UserRepository;
import com.dcl.accommodate.security.jwt.JwtService;
import com.dcl.accommodate.service.contracts.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void registerUser(UserRegistrationRequest registration) {
        if(repository.existsByEmail(registration.email()))
            throw new UserAlreadyExistByEmailException("User already registered with such email ID");
        var user = this.toUser(registration);
        //All users are GUEST by default
        user.setUserRole(UserRole.GUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.email(),request.password());

        var auth = authenticationManager.authenticate(token);

        if(!auth.isAuthenticated())
            throw new UsernameNotFoundException("Failed to authenticate username and password.");

        var user = repository.findByEmail(auth.getName())
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));

        Map<String,Object> claims = new HashMap<>();
        claims.put("email",user.getEmail());
        claims.put("role",user.getUserRole().name());

        return new AuthResponse(
                user.getEmail(),
                jwtService.generateAccessToken(claims, user.getUserId().toString(), Duration.ofSeconds(30)),
                30,
                jwtService.generateRefreshToken(user.getUserId().toString(), Duration.ofHours(1)),
                1);
    }

    private User toUser(UserRegistrationRequest registration) {
        return User.builder()
                .firstName(registration.firstName())
                .lastName(registration.lastName())
                .email(registration.email())
                .phoneNumber(registration.phoneNumber())
                .password(registration.password())
                .dateOfBirth(registration.dateOfBirth())
                .build();
    }
}