package org.service;

import org.dto.request.LoginRequest;
import org.dto.request.RegisterRequest;
import org.result.result;

public interface AuthService {

    result login(LoginRequest loginRequest);

    result register(RegisterRequest registerRequest);

    result changePassword(Long userId, String oldPassword, String newPassword);
}
