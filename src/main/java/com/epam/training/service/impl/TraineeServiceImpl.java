package com.epam.training.service.impl;

import com.epam.training.dto.TraineeDTO;
import com.epam.training.dto.TrainerDTO;
import com.epam.training.dto.request.ActivateDeactiveRequest;
import com.epam.training.dto.request.AuthDTO;
import com.epam.training.dto.request.TraineeCreateDTO;
import com.epam.training.dto.response.ApiResponse;
import com.epam.training.dto.response.GetTraineeTrainerDTO;
import com.epam.training.exception.DomainException;
import com.epam.training.mapper.TraineeMapper;
import com.epam.training.mapper.TrainerMapper;
import com.epam.training.repository.TraineeDao;
import com.epam.training.domain.Trainee;
import com.epam.training.domain.Trainer;
import com.epam.training.domain.Training;
import com.epam.training.domain.User;
import com.epam.training.repository.UserDao;
import com.epam.training.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service("traineeService")
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private static final String ENTITY_NAME = "Trainee";
    private final TraineeDao traineeDao;
    private final TraineeMapper traineeMapper;
    private final UserDao userDao;
    private final TrainerMapper trainerMapper;

    @Override
    @Transactional(readOnly = false)
    public ApiResponse<AuthDTO> createProfile(TraineeCreateDTO dto) {
        LOGGER.info("Request to create {} profile with data: {}", ENTITY_NAME, dto);

        Trainee trainee = new Trainee(new User(dto.getFirstName(), dto.getLastName(), true), dto.getBirthDate(), dto.getAddress());
        trainee.getUser().setUsername(generateUsername(dto.getFirstName(), dto.getLastName()));
        trainee.getUser().setPassword(generatePassword());

        Trainee result = traineeDao.save(trainee);
        AuthDTO response = new AuthDTO(result.getUser().getUsername(), result.getUser().getPassword());
        return new ApiResponse<>(true, null, response);
    }

    @Override
    public ApiResponse<TraineeDTO> getProfile(String username) {
        LOGGER.info("Request to get {} profile with username: {}", ENTITY_NAME, username);

        Trainee trainee = traineeDao.findByUsername(username)
                .orElseThrow(() -> new DomainException("Trainee not found: " + username));

        TraineeDTO dto = traineeMapper.toDto(trainee);
        dto.setTrainerList(getTraineeTrainerDTOS(trainee.getId()));

        return new ApiResponse<>(true, null, dto);
    }

    @Override
    public void checkAuthProfile(String headerUsername, String password, String username) {
        Trainee trainee = getByUsername(username);

        if(trainee.getUser().getUsername().equals(headerUsername) && trainee.getUser().getPassword().equals(password)) {
            return;
        }

        throw new DomainException("Invalid username or password");
    }

    @Override
    public void checkAuthProfile(String headerUsername, String password, Long id) {
        Trainee trainee = traineeDao.findById(id).orElseThrow(() -> new DomainException("Trainee not found: " + id));

        if(trainee.getUser().getUsername().equals(headerUsername) && trainee.getUser().getPassword().equals(password)) {
            return;
        }

        throw new DomainException("Invalid username or password");
    }

    @Override
    public Trainee getByUsername(String username) {
        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new DomainException("Trainee not found: " + username));
    }

    @Override
    public ApiResponse<TraineeDTO> updateProfile(TraineeDTO dto, Long id) {
        LOGGER.info("Request to update {} profile: {}", ENTITY_NAME, dto);

        if (!traineeDao.existsById(id)) {
            throw new DomainException("Trainee not found: " + id);
        }

        Trainee entity = traineeDao.findById(id).get();
        entity.getUser().setFirstName(dto.getFirstName());
        entity.getUser().setLastName(dto.getLastName());
        entity.getUser().setActive(dto.getActive());
        entity.setAddress(dto.getAddress() != null ? dto.getAddress() : entity.getAddress());
        entity.setBirthDate(dto.getBirthDate() != null ? dto.getBirthDate() : entity.getBirthDate());

        traineeDao.update(entity);

        TraineeDTO resultDto = traineeMapper.toDto(entity);
        resultDto.setTrainerList(getTraineeTrainerDTOS(entity.getId()));

        return new ApiResponse<>(true, null, resultDto);
    }

    @Override
    public ApiResponse<Void> deleteTraineeProfile(String username) {
        LOGGER.info("Request to delete {} profile with username: {}", ENTITY_NAME, username);

        if (!traineeDao.existsByUsername(username)) {
            throw new DomainException("Trainee not found: " + username);
        }

        traineeDao.deleteByUsername(username);

        return new ApiResponse<>(true, null, null);
    }

    @Override
    public ApiResponse<Void> activateOrDeactivate(ActivateDeactiveRequest request) {
        LOGGER.info("Request to set {} active status to {} for username: {}",
                ENTITY_NAME, request.getActive(), request.getUsername());

        if (!traineeDao.existsByUsername(request.getUsername())) {
            throw new DomainException("Trainee not found: " + request.getUsername());
        }

        traineeDao.findByUsername(request.getUsername()).ifPresent(trainee -> {
            trainee.getUser().setActive(request.getActive());
            traineeDao.update(trainee);
        });

        return new ApiResponse<>(true, null, null);
    }

    @Override
    public ApiResponse<List<TrainerDTO>> getNotAssignedActiveTrainers(String username) {
        LOGGER.info("Request to get available trainers for {} with username: {}", ENTITY_NAME, username);
        List<Trainer> availableTrainersForTrainee = traineeDao.findAvailableTrainersForTrainee(username);

        List<TrainerDTO> dtos = availableTrainersForTrainee
                .stream()
                .map(trainer -> {
                    TrainerDTO trainerDTO = new TrainerDTO();
                    trainerDTO.setId(trainer.getId());
                    trainerDTO.setActive(trainer.getUser().getActive());
                    trainerDTO.setUsername(trainer.getUser().getUsername());
                    trainerDTO.setFirstName(trainer.getUser().getFirstName());
                    trainerDTO.setLastName(trainer.getUser().getLastName());
                    trainerDTO.setTrainingTypeId(trainer.getSpecialization().getId());

                    return trainerDTO;
                })
                .collect(Collectors.toList());

        return new ApiResponse<>(true, null, dtos);
    }

    public List<Training> getTraineeTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate,
                                              String trainerName, String trainingType) {
        LOGGER.info("Request to get trainings for {} with username: {}", ENTITY_NAME, username);

        return traineeDao.findTraineeTrainings(username, fromDate, toDate, trainerName, trainingType);
    }

    public List<Trainer> getAvailableTrainersForTrainee(String traineeUsername) {
        LOGGER.info("Request to get available trainers for {} with username: {}", ENTITY_NAME, traineeUsername);
        return traineeDao.findAvailableTrainersForTrainee(traineeUsername);
    }

    public void updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        LOGGER.info("Request to update trainers for {} with username: {}", ENTITY_NAME, traineeUsername);
        traineeDao.updateTraineeTrainers(traineeUsername, trainerUsernames);
    }

    private List<GetTraineeTrainerDTO> getTraineeTrainerDTOS(Long traineeId) {
        return traineeDao.findAllTraineeTrainers(traineeId)
                .stream()
                .map(trainerMapper::toGetTraineeTrainerDto)
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