package com.epam.training.mapper;

import com.epam.training.domain.Training;
import com.epam.training.dto.TrainingDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {

    Training toEntity(TrainingDTO dto);
    TrainingDTO toDto(Training entity);
}
