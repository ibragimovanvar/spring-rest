package com.epam.training.controller;

import com.epam.training.dto.TrainingDTO;
import com.epam.training.dto.filters.TraineeTrainingsFilter;
import com.epam.training.dto.filters.TrainerTrainingsFilter;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.dto.response.TraineeFilterResponseDTO;
import com.epam.training.dto.response.TrainerFilterResponseDTO;
import com.epam.training.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @GetMapping("/trainee-trainings")
    public ResponseEntity<ApiResponse<List<TraineeFilterResponseDTO>>> getTraineeTrainings(@Valid @RequestBody TraineeTrainingsFilter filter){
        ApiResponse<List<TraineeFilterResponseDTO>> response = trainingService.getTraineeTrainings(filter);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/trainer-trainings")
    public ResponseEntity<ApiResponse<List<TrainerFilterResponseDTO>>> getTrainerTrainings(@Valid @RequestBody TrainerTrainingsFilter filter){
        ApiResponse<List<TrainerFilterResponseDTO>> response = trainingService.getTrainerTrainings(filter);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createTraining(@Valid @RequestBody TrainingDTO dto){
        ApiResponse<Void> response = trainingService.addTraining(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
