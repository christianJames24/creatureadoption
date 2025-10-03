package com.creatureadoption.apigateway.presentationlayer.trainings;


import com.creatureadoption.apigateway.domainclientlayer.trainings.Difficulty;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingCategory;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingStatus;
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