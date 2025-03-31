package com.epam.training.service;

import com.epam.training.domain.TrainingType;
import com.epam.training.dto.TrainingTypeDTO;
import com.epam.training.dto.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("trainingTypeService")
public interface TrainingTypeService {
    ApiResponse<List<TrainingTypeDTO>> getTrainingTypes();
    TrainingType getTrainingType(Long id);
}
