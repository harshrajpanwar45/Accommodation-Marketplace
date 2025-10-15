
package com.dcl.accommodate.controller;

import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.request.UserUpdateRequest;
import com.dcl.accommodate.dto.response.AuthResponse;
import com.dcl.accommodate.dto.response.UserResponse;
import com.dcl.accommodate.dto.wrapper.ApiAck;
import com.dcl.accommodate.dto.wrapper.ApiResponse;
import com.dcl.accommodate.service.contracts.UserService;
import com.dcl.accommodate.util.UriBuilder;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${app.base-uri}")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UriBuilder uriBuilder;

    @PostMapping("/public/register")
    public ResponseEntity<ApiAck> registerUser(@Valid @RequestBody UserRegistrationRequest user){
        userService.registerUser(user);
        return ResponseEntity.created(uriBuilder.buildURI("/profile"))
                .body(new ApiAck(true,"user registered successfully"));
    }

    @PostMapping("/public/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid @RequestBody UserLoginRequest request){
        var response = userService.loginUser(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "User Successfully Logged In",
                response
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshLogin() {
        var response = userService.refreshLogin();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "User Successfully Logged In",
                response
        ));
    }

    @GetMapping("/getUserProfile")
    public ResponseEntity<ApiResponse<UserResponse>> userProfile(){
        var response = userService.userProfile();
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "User found successfully",
                response
        ));
    }

    @PutMapping("/updateUserProfile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(@Valid @RequestBody UserUpdateRequest request){
        var response = userService.updateUser(request);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "User updated successfully",
                response
        ));
    }
}
