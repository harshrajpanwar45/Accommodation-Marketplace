
package com.dcl.accommodate.service.implementation;

import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.request.UserUpdateRequest;
import com.dcl.accommodate.dto.response.AuthResponse;
import com.dcl.accommodate.dto.response.UserResponse;
import com.dcl.accommodate.enums.UserRole;
import com.dcl.accommodate.exceptions.UserAlreadyExistByEmailException;
import com.dcl.accommodate.model.User;
import com.dcl.accommodate.repository.UserRepository;
import com.dcl.accommodate.security.jwt.JwtService;
import com.dcl.accommodate.security.jwt.JwtType;
import com.dcl.accommodate.service.contracts.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static com.dcl.accommodate.security.util.CurrentUser.getCurrentUserId;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void registerUser(UserRegistrationRequest registration) {
        if (repository.existsByEmail(registration.email()))
            throw new UserAlreadyExistByEmailException("User already registered with such email ID");
        var user = this.toUser(registration);
        //All users are GUEST by default
        user.setUserRole(UserRole.GUEST);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    @Override
    public AuthResponse loginUser(UserLoginRequest request) {
        var token = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var auth = authenticationManager.authenticate(token);

        if (!auth.isAuthenticated())
            throw new UsernameNotFoundException("Failed to authenticate username and password.");

        var user = repository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return grantTokens(user);
    }

    @Override
    public AuthResponse refreshLogin() {
        Supplier<UsernameNotFoundException> userNotFound = () -> new UsernameNotFoundException("User not found");

        UUID userId = getCurrentUserId().orElseThrow(userNotFound);
        User user = repository.findById(userId).orElseThrow(userNotFound);

        return grantTokens(user);
    }

    @Override
    public UserResponse userProfile() {
        UUID userId = getCurrentUserId().get();

        User user = repository.findById(userId).get();
        return toUserResponse(user);
    }

    @Override
    public UserResponse updateUser(UserUpdateRequest request) {
        UUID userId = getCurrentUserId().get();

        User user = repository.findById(userId).get();
        mergeRequestToUser(user, request);

        user = repository.save(user);

        return toUserResponse(user);
    }

    private static void mergeRequestToUser(User user, UserUpdateRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setDateOfBirth(request.dateOfBirth());
        user.setPhoneNumber(request.phoneNumber());
    }

    private static UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .phoneNumber(user.getPhoneNumber())
                .userRole(user.getUserRole())
                .avatar(user.getAvatar())
                .lastModifiedDate(user.getLastModifiedDate())
                .createdDate(user.getCreatedDate())
                .build();
    }

    private AuthResponse grantTokens(User user) {
        JwtService.TokenResult accessToken = generateAccessToken(user);
        JwtService.TokenResult refreshToken = generateRefreshToken(user);

        return new AuthResponse(
                user.getUserId().toString(),
                accessToken.token(),
                accessToken.ttl().toSeconds(),
                refreshToken.token(),
                refreshToken.ttl().toSeconds()
        );
    }

    private JwtService.TokenResult generateRefreshToken(User user) {
        var tokenConfig = new JwtService.TokenConfig(
                new HashMap<>(),
                user.getUserId().toString(),
                JwtType.REFRESH
        );

        return jwtService.generateToken(tokenConfig);
    }

    private JwtService.TokenResult generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("role", user.getUserRole().name());

        var tokenConfig = new JwtService.TokenConfig(
                claims,
                user.getUserId().toString(),
                JwtType.ACCESS
        );

        return jwtService.generateToken(tokenConfig);
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
