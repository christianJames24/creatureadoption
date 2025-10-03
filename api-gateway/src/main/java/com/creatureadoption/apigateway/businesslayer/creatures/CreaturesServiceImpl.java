package com.creatureadoption.apigateway.businesslayer.creatures;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreaturesServiceClient;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureRequestModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureResponseModel;
import com.creatureadoption.apigateway.utils.exceptions.EntityInUseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CreaturesServiceImpl implements CreaturesService {

    private final CreaturesServiceClient creaturesServiceClient;
    private final AdoptionsServiceClient adoptionsServiceClient;

    public CreaturesServiceImpl(CreaturesServiceClient creaturesServiceClient, AdoptionsServiceClient adoptionsServiceClient) {
        this.creaturesServiceClient = creaturesServiceClient;
        this.adoptionsServiceClient = adoptionsServiceClient;
    }

    @Override
    public List<CreatureResponseModel> getCreatures(Map<String, String> queryParams) {
        return creaturesServiceClient.getCreatures(queryParams);
    }

    @Override
    public CreatureResponseModel getCreatureByCreatureId(String creatureId) {
        return creaturesServiceClient.getCreatureByCreatureId(creatureId);
    }

    @Override
    public CreatureResponseModel addCreature(CreatureRequestModel creatureRequestModel) {
        return creaturesServiceClient.addCreature(creatureRequestModel);
    }

    @Override
    public CreatureResponseModel updateCreature(CreatureRequestModel creatureRequestModel, String creatureId) {
        return creaturesServiceClient.updateCreature(creatureRequestModel, creatureId);
    }

    @Override
    public void removeCreature(String creatureId) {
        // Check if creature has any adoptions
        Map<String, String> queryParams = Map.of("creatureId", creatureId);
        List<AdoptionResponseModel> adoptions = adoptionsServiceClient.getAdoptions(queryParams);

        if (!adoptions.isEmpty()) {
            throw new EntityInUseException("Cannot delete creature with ID: " + creatureId +
                    " because it has existing adoptions");
        }

        creaturesServiceClient.removeCreature(creatureId);
    }
}