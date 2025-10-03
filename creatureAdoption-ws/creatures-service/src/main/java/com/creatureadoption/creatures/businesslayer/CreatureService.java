package com.creatureadoption.creatures.businesslayer;

import com.creatureadoption.creatures.presentationlayer.CreatureRequestModel;
import com.creatureadoption.creatures.presentationlayer.CreatureResponseModel;

import java.util.List;
import java.util.Map;

public interface CreatureService {

    List<CreatureResponseModel> getCreatures(Map<String, String> queryParams);
    CreatureResponseModel getCreatureByCreatureId(String creatureId);
    CreatureResponseModel addCreature(CreatureRequestModel creatureRequestModel);
    CreatureResponseModel updateCreature(CreatureRequestModel updatedCreature, String creatureId);
    void removeCreature(String creatureId);
}