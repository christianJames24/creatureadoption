package com.creatureadoption.trainings.presentationlayer;

import com.creatureadoption.trainings.dataaccesslayer.Difficulty;
import com.creatureadoption.trainings.dataaccesslayer.TrainingCategory;
import com.creatureadoption.trainings.dataaccesslayer.TrainingStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrainingRequestModel {

    String name;
    String description;
    Difficulty difficulty;
    Integer duration;
    TrainingStatus status;
    TrainingCategory category;
    Double price;
    String location;
}