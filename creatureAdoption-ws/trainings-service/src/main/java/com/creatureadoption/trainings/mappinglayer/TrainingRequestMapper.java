package com.creatureadoption.trainings.mappinglayer;

import com.creatureadoption.trainings.dataaccesslayer.Training;
import com.creatureadoption.trainings.dataaccesslayer.TrainingIdentifier;
import com.creatureadoption.trainings.presentationlayer.TrainingRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TrainingRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    Training requestModelToEntity(TrainingRequestModel trainingRequestModel, TrainingIdentifier trainingIdentifier);
}