package com.creatureadoption.trainings.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Integer> {

    Training findByTrainingIdentifier_TrainingId(String trainingId);
}