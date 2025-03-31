package com.epam.training.service;

import com.epam.training.domain.Trainee;
import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.request.ActivateDeactiveRequest;
import com.epam.training.dto.request.AuthDTO;
import com.epam.training.dto.request.TraineeCreateDTO;
import com.epam.training.dto.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("traineeService")
public interface TraineeService {
    ApiResponse<TraineeDTO> getProfile(String username);
    Trainee getByUsername(String username);
    ApiResponse<TraineeDTO> updateProfile(TraineeDTO trainee, Long id);
    ApiResponse<AuthDTO> createProfile(TraineeCreateDTO dto);
    ApiResponse<Void> deleteTraineeProfile(String username);
    ApiResponse<Void> activateOrDeactivate(ActivateDeactiveRequest request);
    ApiResponse<List<TrainerDTO>> getNotAssignedActiveTrainers(String username);
}
