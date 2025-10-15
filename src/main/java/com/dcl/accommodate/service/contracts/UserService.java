package com.dcl.accommodate.service.contracts;


import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.request.UserUpdateRequest;
import com.dcl.accommodate.dto.response.AuthResponse;
import com.dcl.accommodate.dto.response.UserResponse;

public interface UserService {

    public void registerUser(UserRegistrationRequest registration);

    public AuthResponse loginUser(UserLoginRequest request);

    public AuthResponse refreshLogin();

    UserResponse userProfile();

    UserResponse updateUser(UserUpdateRequest request);
}
