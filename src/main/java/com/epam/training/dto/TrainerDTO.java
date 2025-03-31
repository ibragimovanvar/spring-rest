package com.epam.training.dto;


import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {
    private Long id;

    @NotNull(message = "Please enter your first name")
    private String firstName;

    @NotNull(message = "Please enter your first name")
    private String lastName;

    @NotNull(message = "Please enter your username")
    private String username;

    @NotNull(message = "Please select training type")
    private Long trainingTypeId;

    @NotNull(message = "Please select active/inactive")
    private Boolean active;

    private List<TraineeDTO> traineeList;
}