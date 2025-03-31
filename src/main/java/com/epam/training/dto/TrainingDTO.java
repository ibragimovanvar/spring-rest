package com.epam.training.dto;

import com.epam.training.domain.TrainingType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingDTO {
    private Long id;

    @NotNull(message = "Please enter trainer username")
    private String trainerUsername;

    @NotNull(message = "Please enter trainee username")
    private String traineeUsername;

    @NotNull(message = "Please enter training name")
    private String trainingName;

    @NotNull(message = "Please enter training date time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime trainingDateTime;

    @NotNull(message = "Please enter training duration(minutes)")
    private Integer trainingDurationInHours;
}
