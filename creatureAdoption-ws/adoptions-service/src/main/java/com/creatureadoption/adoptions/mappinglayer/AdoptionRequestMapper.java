package com.creatureadoption.adoptions.mappinglayer;

import com.creatureadoption.adoptions.dataaccesslayer.Adoption;
import com.creatureadoption.adoptions.dataaccesslayer.AdoptionIdentifier;
import com.creatureadoption.adoptions.presentationlayer.AdoptionRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AdoptionRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    })
    Adoption requestModelToEntity(AdoptionRequestModel adoptionRequestModel, AdoptionIdentifier adoptionIdentifier);
}