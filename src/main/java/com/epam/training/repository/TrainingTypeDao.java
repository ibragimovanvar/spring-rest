package com.epam.training.repository;

import com.epam.training.domain.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingTypeDao {
    Optional<TrainingType> findById(Long id);
    List<TrainingType> findAll();
}