package com.creatureadoption.creatures.mappinglayer;

import com.creatureadoption.creatures.dataaccesslayer.Creature;
import com.creatureadoption.creatures.dataaccesslayer.CreatureIdentifier;
import com.creatureadoption.creatures.dataaccesslayer.CreatureTraits;
import com.creatureadoption.creatures.presentationlayer.CreatureRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CreatureRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
    })
    Creature requestModelToEntity(CreatureRequestModel creatureRequestModel, CreatureIdentifier creatureIdentifier,
                                  CreatureTraits creatureTraits);
}