package com.epam.training.mapper;

import com.epam.training.domain.Trainee;
import com.epam.training.dto.TraineeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TraineeMapper {

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.active", target = "active")
    TraineeDTO toDto(Trainee trainee);

    Trainee toEntity(TraineeDTO dto);
}
