package com.creatureadoption.trainings.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class TrainingIdentifier {

    private String trainingId;
    private String trainingCode;

    public TrainingIdentifier() {
        this.trainingId = UUID.randomUUID().toString();
        this.trainingCode = "TRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public TrainingIdentifier(String trainingId, String trainingCode) {
        this.trainingId = trainingId;
        this.trainingCode = trainingCode;
    }
}