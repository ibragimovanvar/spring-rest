package com.epam.training.repository;

import com.epam.training.domain.Training;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingDao {
    Training save(Training training);

    Optional<Training> findById(Long id);

    void update(Training training);

    void delete(Training training);

    List<Training> findAll();
}