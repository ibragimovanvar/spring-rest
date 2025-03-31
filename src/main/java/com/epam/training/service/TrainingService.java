package com.epam.training.service;

import com.epam.training.domain.Training;
import com.epam.training.dto.TrainingDTO;
import com.epam.training.dto.filters.TraineeTrainingsFilter;
import com.epam.training.dto.filters.TrainerTrainingsFilter;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.dto.response.TraineeFilterResponseDTO;
import com.epam.training.dto.response.TrainerFilterResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("trainingService")
public interface TrainingService {
    ApiResponse<List<TraineeFilterResponseDTO>> getTraineeTrainings(TraineeTrainingsFilter filter);
    ApiResponse<List<TrainerFilterResponseDTO>> getTrainerTrainings(TrainerTrainingsFilter filter);
    ApiResponse<Void> addTraining(TrainingDTO dto);
}
