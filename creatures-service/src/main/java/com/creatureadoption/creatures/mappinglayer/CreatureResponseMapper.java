package com.creatureadoption.creatures.mappinglayer;

import com.creatureadoption.creatures.dataaccesslayer.Creature;
import com.creatureadoption.creatures.presentationlayer.CreatureController;
import com.creatureadoption.creatures.presentationlayer.CreatureResponseModel;
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
public interface CreatureResponseMapper {

    @Mapping(expression = "java(creature.getCreatureIdentifier().getCreatureId())", target = "creatureId")
    @Mapping(expression = "java(creature.getCreatureIdentifier().getRegistrationCode())", target = "registrationCode")
    @Mapping(expression = "java(creature.getCreatureTraits().getStrength())", target = "strength")
    @Mapping(expression = "java(creature.getCreatureTraits().getIntelligence())", target = "intelligence")
    @Mapping(expression = "java(creature.getCreatureTraits().getAgility())", target = "agility")
    @Mapping(expression = "java(creature.getCreatureTraits().getTemperament())", target = "temperament")
    CreatureResponseModel entityToResponseModel(Creature creature);

    List<CreatureResponseModel> entityListToResponseModelList(List<Creature> creatures);

    @AfterMapping
    default void addLinks(@MappingTarget CreatureResponseModel response, Creature creature) {
        Link selfLink = linkTo(methodOn(CreatureController.class)
                .getCreatureByCreatureId(response.getCreatureId()))
                .withSelfRel();
        response.add(selfLink);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("creatureId", response.getCreatureId());

        Link allCreaturesLink = linkTo(methodOn(CreatureController.class)
                .getCreatures(queryParams))
                .withRel("allCreatures");
        response.add(allCreaturesLink);
    }
}