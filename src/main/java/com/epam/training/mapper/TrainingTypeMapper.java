package com.epam.training.mapper;

import com.epam.training.domain.TrainingType;
import com.epam.training.dto.TrainingTypeDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    TrainingType toEntity(TrainingTypeDTO trainingTypeDTO);
    TrainingTypeDTO toDto(TrainingType trainingType);
}
