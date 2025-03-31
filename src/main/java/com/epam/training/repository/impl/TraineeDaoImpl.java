package com.epam.training.repository.impl;

import com.epam.training.repository.TraineeDao;
import com.epam.training.domain.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class TraineeDaoImpl implements TraineeDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional(readOnly = false)
    public Trainee save(Trainee trainee) {
        trainee.getUser().setRole("ROLE_TRAINEE");
        entityManager.persist(trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return entityManager.createQuery("SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void update(Trainee trainee) {
        entityManager.merge(trainee);
    }

    @Override
    public void delete(Trainee trainee) {
        entityManager.remove(entityManager.contains(trainee) ? trainee : entityManager.merge(trainee));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void deleteByUsername(String username) {
        entityManager.createNativeQuery(
                "DELETE FROM trainings WHERE trainee_id IN (SELECT t.user_id FROM trainees t JOIN app_users u ON t.user_id = u.id WHERE u.username = ?)"
        ).setParameter(1, username).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM trainees_trainers WHERE trainee_user_id IN (SELECT t.user_id FROM trainees t JOIN app_users u ON t.user_id = u.id WHERE u.username = ?)"
        ).setParameter(1, username).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM trainees WHERE user_id IN (SELECT id FROM app_users WHERE username = ?)"
        ).setParameter(1, username).executeUpdate();

        entityManager.createNativeQuery(
                "DELETE FROM app_users WHERE username = ?"
        ).setParameter(1, username).executeUpdate();
    }

    @Override
    public List<Trainee> findAll() {
        return entityManager.createQuery("SELECT t FROM Trainee t", Trainee.class)
                .getResultList();
    }

    @Override
    public boolean existsByUsername(String username) {
        return entityManager.createQuery("SELECT COUNT(t) FROM Trainee t WHERE t.user.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsById(Long id) {
        return entityManager.createQuery("SELECT COUNT(t) FROM Trainee t WHERE t.user.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    @Override
    public List<Training> findTraineeTrainings(String username, LocalDateTime fromDate, LocalDateTime toDate,
                                               String trainerName, String trainingType) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> root = cq.from(Training.class);
        Join<Training, Trainee> traineeJoin = root.join("trainee");
        Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");
        Join<Training, Trainer> trainerJoin = root.join("trainer");
        Join<Trainer, User> trainerUserJoin = trainerJoin.join("user");
        Join<Training, TrainingType> typeJoin = root.join("trainingType");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(traineeUserJoin.get("username"), username));

        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDateTime"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trainingDateTime"), toDate));
        }
        if (trainerName != null && !trainerName.isEmpty()) {
            predicates.add(cb.like(cb.lower(trainerUserJoin.get("firstName")), "%" + trainerName.toLowerCase() + "%"));
        }
        if (trainingType != null && !trainingType.isEmpty()) {
            predicates.add(cb.equal(typeJoin.get("trainingTypeName"), trainingType));
        }

        cq.select(root).where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }

    @Override
    public List<Trainer> findAvailableTrainersForTrainee(String traineeUsername) {
        return entityManager.createQuery(
                        "SELECT tr FROM Trainer tr WHERE tr NOT IN " +
                                "(SELECT t.trainers FROM Trainee t WHERE t.user.username = :username)",
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .getResultList();
    }

    @Override
    public void updateTraineeTrainers(String username, List<String> trainerUsernames) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        List<Trainer> trainers = entityManager.createQuery(
                        "SELECT t FROM Trainer t WHERE t.user.username IN :usernames", Trainer.class)
                .setParameter("usernames", trainerUsernames)
                .getResultList();
        trainee.setTrainers(trainers);
        entityManager.merge(trainee);
    }
}