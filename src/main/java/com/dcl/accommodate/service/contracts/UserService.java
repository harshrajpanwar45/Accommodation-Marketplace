package com.dcl.accommodate.service.contracts;


import com.dcl.accommodate.dto.request.UserLoginRequest;
import com.dcl.accommodate.dto.request.UserRegistrationRequest;
import com.dcl.accommodate.dto.response.AuthResponse;

public interface UserService {

    public void registerUser(UserRegistrationRequest registration);

    public AuthResponse loginUser(UserLoginRequest request);
}
