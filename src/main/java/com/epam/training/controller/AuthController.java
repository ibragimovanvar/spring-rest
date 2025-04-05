package com.epam.training.controller;

import com.epam.training.dto.request.AuthDTO;
import com.epam.training.dto.request.PasswordChangeRequest;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.repository.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserDao userDao;

    @Operation(summary = "Login endpoint", description = "Returns a login token")
    @GetMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody AuthDTO authDTO) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setData(null);

        if (userDao.existsByUsernameAndPassword(authDTO.getUsername(), authDTO.getPassword())) {
            apiResponse.setMessage("Login Successful");
            apiResponse.setSuccess(true);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            apiResponse.setMessage("Login or password wrong!");
            apiResponse.setSuccess(false);
            return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody PasswordChangeRequest request) {
        ApiResponse<Void> apiResponse = new ApiResponse<>();
        apiResponse.setData(null);

        if (userDao.updatePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword())) {
            apiResponse.setMessage("Password changed successfully");
            apiResponse.setSuccess(true);
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } else {
            apiResponse.setMessage("Failed to change password. Please verify your username and old password.");
            apiResponse.setSuccess(false);
            return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
