package com.epam.training.controller;

import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.request.ActivateDeactiveRequest;
import com.epam.training.dto.request.AuthDTO;
import com.epam.training.dto.request.TrainerCreateDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @PostMapping
    public ResponseEntity<ApiResponse<AuthDTO>> createTrainer(@Valid @RequestBody TrainerCreateDTO createDTO) {
        ApiResponse<AuthDTO> response = trainerService.createProfile(createDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/by-username")
    public ResponseEntity<ApiResponse<TrainerDTO>> getTrainerByUsername(@RequestHeader(value = "username", required = true) String headerUsername, @RequestHeader(value = "password", required = true) String password, @RequestParam(required = true) String username) {
        trainerService.checkAuthProfile(headerUsername, password, username);

        ApiResponse<TrainerDTO> response = trainerService.getProfile(username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TrainerDTO>> updateTrainer(@RequestHeader(value = "username", required = true) String headerUsername, @RequestHeader(value = "password", required = true) String password, @Valid @RequestBody TrainerDTO trainerDTO, @PathVariable Long id) {
        trainerService.checkAuthProfile(headerUsername, password, id);

        ApiResponse<TrainerDTO> response = trainerService.updateProfile(trainerDTO, id);
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTrainer(@RequestHeader(value = "username", required = true) String headerUsername, @RequestHeader(value = "password", required = true) String password, @RequestParam(required = true) String username) {
        trainerService.checkAuthProfile(headerUsername, password, username);


        ApiResponse<Void> response = trainerService.deleteProfile(username);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    public ResponseEntity<ApiResponse<Void>> activateOrDeactivate(@RequestHeader(value = "username", required = true) String headerUsername, @RequestHeader(value = "password", required = true) String password, @Valid @RequestBody ActivateDeactiveRequest request) {
        trainerService.checkAuthProfile(headerUsername, password, request.getUsername());

        ApiResponse<Void> response = trainerService.activateOrDeactivate(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
