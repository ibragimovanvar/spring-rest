package com.epam.training.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTraineeTrainers {

    @NotNull(message = "Please enter username")
    private String traineeUsername;

    @NotNull(message = "Trainers are required")
    private List<String> trainersList;
}
