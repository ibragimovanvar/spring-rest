package com.epam.training.dto.request;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivateDeactiveRequest {

    @NotNull(message = "Please enter your username")
    private String username;

    @NotNull(message = "Please select active/inactive")
    private Boolean active;

}
