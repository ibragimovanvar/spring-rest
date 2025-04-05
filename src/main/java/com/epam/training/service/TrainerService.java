package com.epam.training.service;

import com.epam.training.domain.Trainer;
import com.epam.training.domain.Training;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.request.ActivateDeactiveRequest;
import com.epam.training.dto.request.AuthDTO;
import com.epam.training.dto.request.TrainerCreateDTO;
import com.epam.training.dto.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("trainerService")
public interface TrainerService {
    void checkAuthProfile(String headerUsername, String password, String username);
    void checkAuthProfile(String headerUsername, String password, Long id);
    ApiResponse<TrainerDTO> getProfile(String username);
    Trainer getByUsername(String username);
    ApiResponse<TrainerDTO> updateProfile(TrainerDTO dto, Long id);
    ApiResponse<AuthDTO> createProfile(TrainerCreateDTO dto);
    ApiResponse<Void> deleteProfile(String username);
    ApiResponse<Void> activateOrDeactivate(ActivateDeactiveRequest request);
}
