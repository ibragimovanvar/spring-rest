package com.epam.training.repository;

import com.epam.training.domain.Trainee;
import com.epam.training.domain.Trainer;
import com.epam.training.domain.Training;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerDao {
    Trainer save(Trainer trainer);

    Optional<Trainer> findById(Long id);

    Optional<Trainer> findByUsername(String username);
    List<Trainee> findAllTrainerTrainees(Long id);

    Trainer update(Trainer trainer);

    void delete(Trainer trainer);

    List<Trainer> findAll();

    boolean existsByUsername(String username);

    List<Training> findTrainerTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate, String traineeName);
}