package com.epam.training.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetTraineeTrainerDTO {
    @NotNull(message = "Please enter your first name")
    private String firstName;

    @NotNull(message = "Please enter your first name")
    private String lastName;

    @NotNull(message = "Please enter your username")
    private String username;

    @NotNull(message = "Please select training type")
    private Long trainingTypeId;
}