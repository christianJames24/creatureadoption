package com.creatureadoption.apigateway.presentationlayer.trainings;

import com.creatureadoption.apigateway.domainclientlayer.trainings.Difficulty;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingCategory;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class TrainingResponseModel extends RepresentationModel<TrainingResponseModel> {

    String trainingId;
    String trainingCode;
    String name;
    String description;
    Difficulty difficulty;
    Integer duration;
    TrainingStatus status;
    TrainingCategory category;
    Double price;
    String location;
}