package com.creatureadoption.trainings.mappinglayer;

import com.creatureadoption.trainings.dataaccesslayer.Training;
import com.creatureadoption.trainings.presentationlayer.TrainingController;
import com.creatureadoption.trainings.presentationlayer.TrainingResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface TrainingResponseMapper {

    @Mapping(expression = "java(training.getTrainingIdentifier().getTrainingId())", target = "trainingId")
    @Mapping(expression = "java(training.getTrainingIdentifier().getTrainingCode())", target = "trainingCode")
    TrainingResponseModel entityToResponseModel(Training training);

    List<TrainingResponseModel> entityListToResponseModelList(List<Training> trainings);

    @AfterMapping
    default void addLinks(@MappingTarget TrainingResponseModel response, Training training) {
        Link selfLink = linkTo(methodOn(TrainingController.class)
                .getTrainingByTrainingId(response.getTrainingId()))
                .withSelfRel();
        response.add(selfLink);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("trainingId", response.getTrainingId());

        Link allTrainingsLink = linkTo(methodOn(TrainingController.class)
                .getTrainings(queryParams))
                .withRel("allTrainings");
        response.add(allTrainingsLink);
    }
}