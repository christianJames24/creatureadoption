package com.creatureadoption.apigateway.businesslayer.creatures;

import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureRequestModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureResponseModel;

import java.util.List;
import java.util.Map;

public interface CreaturesService {
    List<CreatureResponseModel> getCreatures(Map<String, String> queryParams);
    CreatureResponseModel getCreatureByCreatureId(String creatureId);
    CreatureResponseModel addCreature(CreatureRequestModel creatureRequestModel);
    CreatureResponseModel updateCreature(CreatureRequestModel creatureRequestModel, String creatureId);
    void removeCreature(String creatureId);
}