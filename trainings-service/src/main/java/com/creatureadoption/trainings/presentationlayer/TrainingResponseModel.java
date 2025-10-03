package com.creatureadoption.trainings.presentationlayer;

import com.creatureadoption.trainings.dataaccesslayer.Difficulty;
import com.creatureadoption.trainings.dataaccesslayer.TrainingCategory;
import com.creatureadoption.trainings.dataaccesslayer.TrainingStatus;
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