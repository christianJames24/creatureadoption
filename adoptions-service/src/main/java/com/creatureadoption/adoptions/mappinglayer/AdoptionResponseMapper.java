package com.creatureadoption.adoptions.mappinglayer;

import com.creatureadoption.adoptions.dataaccesslayer.Adoption;
import com.creatureadoption.adoptions.presentationlayer.AdoptionController;
import com.creatureadoption.adoptions.presentationlayer.AdoptionResponseModel;
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
public interface AdoptionResponseMapper {

    @Mapping(expression = "java(adoption.getAdoptionIdentifier().getAdoptionId())", target = "adoptionId")
    @Mapping(expression = "java(adoption.getAdoptionIdentifier().getAdoptionCode())", target = "adoptionCode")
    @Mapping(target = "customerFirstName", ignore = true)
    @Mapping(target = "customerLastName", ignore = true)
    @Mapping(target = "creatureName", ignore = true)
    @Mapping(target = "creatureSpecies", ignore = true)
    @Mapping(target = "creatureStatus", ignore = true)
    @Mapping(target = "trainingName", ignore = true)
    @Mapping(target = "trainingLocation", ignore = true)
    AdoptionResponseModel entityToResponseModel(Adoption adoption);

    List<AdoptionResponseModel> entityListToResponseModelList(List<Adoption> adoptions);

    @AfterMapping
    default void addLinks(@MappingTarget AdoptionResponseModel response, Adoption adoption) {
        Link selfLink = linkTo(methodOn(AdoptionController.class)
                .getAdoptionByAdoptionId(response.getAdoptionId()))
                .withSelfRel();
        response.add(selfLink);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("adoptionId", response.getAdoptionId());

        Link allAdoptionsLink = linkTo(methodOn(AdoptionController.class)
                .getAdoptions(queryParams))
                .withRel("allAdoptions");
        response.add(allAdoptionsLink);
    }
}