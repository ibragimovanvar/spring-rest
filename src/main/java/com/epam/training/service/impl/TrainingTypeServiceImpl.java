package com.epam.training.service.impl;

import com.epam.training.domain.TrainingType;
import com.epam.training.dto.TrainingTypeDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.exception.DomainException;
import com.epam.training.mapper.TrainingTypeMapper;
import com.epam.training.repository.TrainingTypeDao;
import com.epam.training.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingTypeMapper trainingTypeMapper;

    @Override
    public ApiResponse<List<TrainingTypeDTO>> getTrainingTypes() {

        List<TrainingTypeDTO> dtoList = trainingTypeDao
                .findAll()
                .stream()
                .map(trainingTypeMapper::toDto)
                .collect(Collectors.toList());

        return new ApiResponse<>(true, null, dtoList);
    }

    @Override
    public TrainingType getTrainingType(Long id) {
        Optional<TrainingType> optionalTrainingType = trainingTypeDao.findById(id);

        if(optionalTrainingType.isEmpty()){
            throw new DomainException("Training type not found");
        }

        return optionalTrainingType.get();
    }
}
