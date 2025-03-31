package com.epam.training.mapper;

import com.epam.training.domain.Trainee;
import com.epam.training.domain.Trainer;
import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.response.GetTraineeTrainerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerMapper {

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.active", target = "active")
    @Mapping(source = "specialization.id", target = "trainingTypeId")
    TrainerDTO toDto(Trainer trainee);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "specialization.id", target = "trainingTypeId")
    GetTraineeTrainerDTO toGetTraineeTrainerDto(Trainer trainee);

    Trainer toEntity(TrainerDTO dto);
}
