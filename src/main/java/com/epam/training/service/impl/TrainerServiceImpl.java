package com.epam.training.service.impl;

import com.epam.training.domain.*;
import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.request.ActivateDeactiveRequest;
import com.epam.training.dto.request.AuthDTO;
import com.epam.training.dto.request.TrainerCreateDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.exception.DomainException;
import com.epam.training.mapper.TraineeMapper;
import com.epam.training.mapper.TrainerMapper;
import com.epam.training.repository.TrainerDao;
import com.epam.training.repository.UserDao;
import com.epam.training.service.TrainerService;
import com.epam.training.service.TrainingTypeService;
import com.epam.training.util.DomainUtils;
import com.epam.training.util.StatusTypes;
import com.epam.training.util.OperationTypes;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service("trainerService")
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainerServiceImpl.class);
    private static final String ENTITY_NAME = "Trainer";
    private final TrainerDao trainerDao;
    private final TrainerMapper trainerMapper;
    private final DomainUtils domainUtils;
    private final TrainingTypeService trainingTypeService;
    private final UserDao userDao;
    private final TraineeMapper traineeMapper;

    @Transactional
    @Override
    public ApiResponse<AuthDTO> createProfile(TrainerCreateDTO dto) {
        LOGGER.info("Request to create {} profile with data: {}",
                ENTITY_NAME, dto);

        Trainer trainer = new Trainer(new User(dto.getFirstName(), dto.getLastName(), true), trainingTypeService.getTrainingType(dto.getTrainingTypeId()));
        trainer.getUser().setUsername(generateUsername(dto.getFirstName(), dto.getLastName()));
        trainer.getUser().setPassword(generatePassword());

        Trainer savedTrainer = trainerDao.save(trainer);

        AuthDTO authDTO = new AuthDTO(savedTrainer.getUser().getUsername(), savedTrainer.getUser().getPassword());

        return new ApiResponse<>(true, null, authDTO);
    }

    @Override
    public void checkAuthProfile(String headerUsername, String password, String username) {
        Trainer trainer = getByUsername(username);

        if(trainer.getUser().getUsername().equals(headerUsername) && trainer.getUser().getPassword().equals(password)) {
            return;
        }

        throw new DomainException("Invalid username or password");
    }

    @Override
    public void checkAuthProfile(String headerUsername, String password, Long id) {
        Trainer trainer = trainerDao.findById(id).orElseThrow(() -> new DomainException("Trainee not found: " + id));

        if(trainer.getUser().getUsername().equals(headerUsername) && trainer.getUser().getPassword().equals(password)) {
            return;
        }

        throw new DomainException("Invalid username or password");
    }

    @Override
    public ApiResponse<TrainerDTO> getProfile(String username) {
        LOGGER.info("Request to get {} profile with username: {}", ENTITY_NAME, username);

        Trainer trainer = trainerDao.findByUsername(username)
                .orElseThrow(() -> new DomainException("Trainer not found: " + username));

        TrainerDTO dto = trainerMapper.toDto(trainer);

        dto.setTraineeList(getTraineesTrainer(trainer.getId()));

        return new ApiResponse<>(true, null, dto);
    }

    @Override
    public Trainer getByUsername(String username) {
        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new DomainException("Trainer not found: " + username));
    }


    @Transactional
    @Override
    public ApiResponse<TrainerDTO> updateProfile(TrainerDTO dto, Long id) {
        LOGGER.info("Request to update {} profile: {}", ENTITY_NAME, dto);

        Optional<Trainer> optionalTrainer = trainerDao.findById(id);

        if(optionalTrainer.isEmpty()){
            throw new DomainException("Trainer not found: " + id);
        }
        Trainer trainer = optionalTrainer.get();
        trainer.getUser().setFirstName(dto.getFirstName());
        trainer.getUser().setLastName(dto.getLastName());
        trainer.getUser().setActive(dto.getActive());
        trainer.setSpecialization(trainingTypeService.getTrainingType(dto.getTrainingTypeId()));

        Trainer update = trainerDao.update(trainer);

        TrainerDTO responseDto = trainerMapper.toDto(update);
        responseDto.setTraineeList(getTraineesTrainer(update.getId()));

        return new ApiResponse<>(true, null, responseDto);
    }

    private String checkUsernameNotExists(@NotNull(message = "Please enter your username") String username) {
        if(userDao.existsByUsername(username)){
            throw new DomainException("Username already exists: " + username);
        }

        return username;
    }

    @Transactional
    public List<Training> getTrainerTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate, String traineeName) {
        LOGGER.info("Request to get trainings for {} with username: {}", ENTITY_NAME, username);
        requireAuthentication(username);
        return trainerDao.findTrainerTrainings(username, fromDate, toDate, traineeName);
    }

    @Override
    public ApiResponse<Void> deleteProfile(String username) {
        Optional<Trainer> optionalTrainer = trainerDao.findByUsername(username);

        if(optionalTrainer.isEmpty()){
            throw new DomainException("Trainer not found: " + username);
        }
        Trainer trainer = optionalTrainer.get();
        trainer.getUser().setActive(false);
        trainerDao.update(trainer);

        return new ApiResponse<>(true, domainUtils.getMessage(ENTITY_NAME, StatusTypes.MESSAGE_TYPE_SUCCESS, OperationTypes.DELETION), null);
    }

    @Override
    public ApiResponse<Void> activateOrDeactivate(ActivateDeactiveRequest request) {
        Optional<Trainer> optionalTrainer = trainerDao.findByUsername(request.getUsername());

        if(optionalTrainer.isEmpty()){
            throw new DomainException("Trainer not found: " + request.getUsername());
        }

        Trainer trainer = optionalTrainer.get();
        trainer.getUser().setActive(!trainer.getUser().getActive());
        trainerDao.update(trainer);

        return new ApiResponse<>(true, null, null);

    }

    private void validateRequiredFields(String firstName, String lastName, TrainingType specialization) {
        if (firstName == null || lastName == null || specialization == null) {
            throw new IllegalArgumentException("Required fields are missing");
        }
    }

    private void requireAuthentication(String username) {
        if (!trainerDao.findByUsername(username).isPresent()) {
            throw new SecurityException("Authentication required");
        }
    }

    private List<TraineeDTO> getTraineesTrainer(Long trainerId) {
        return trainerDao.findAllTrainerTrainees(trainerId)
                .stream()
                .map(traineeMapper::toDto)
                .collect(Collectors.toList());
    }

    private synchronized String generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName + "_" + lastName).toLowerCase();
        String username = baseUsername;
        int suffix = 1;

        while (userDao.existsByUsername(username)) {
            username = baseUsername + suffix++;
        }

        LOGGER.info("Generated username: {}", username);
        return username;
    }

    private String generatePassword() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        LOGGER.info("Generated password: {}", sb);
        return sb.toString();
    }
}