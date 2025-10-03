package com.creatureadoption.apigateway.businesslayer.adoptions;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionRequestModel;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdoptionsServiceImpl implements AdoptionsService {

    private final AdoptionsServiceClient adoptionsServiceClient;

    public AdoptionsServiceImpl(AdoptionsServiceClient adoptionsServiceClient) {
        this.adoptionsServiceClient = adoptionsServiceClient;
    }

    @Override
    public List<AdoptionResponseModel> getAdoptions(Map<String, String> queryParams) {
        return adoptionsServiceClient.getAdoptions(queryParams);
    }

    @Override
    public AdoptionResponseModel getAdoptionByAdoptionId(String adoptionId) {
        return adoptionsServiceClient.getAdoptionByAdoptionId(adoptionId);
    }

    @Override
    public AdoptionResponseModel addAdoption(AdoptionRequestModel adoptionRequestModel) {
        return adoptionsServiceClient.addAdoption(adoptionRequestModel);
    }

    @Override
    public AdoptionResponseModel updateAdoption(AdoptionRequestModel adoptionRequestModel, String adoptionId) {
        return adoptionsServiceClient.updateAdoption(adoptionRequestModel, adoptionId);
    }

    @Override
    public AdoptionResponseModel updateAdoptionStatus(String adoptionId, String status) {
        return adoptionsServiceClient.updateAdoptionStatus(adoptionId, status);
    }

    @Override
    public void removeAdoption(String adoptionId) {
        adoptionsServiceClient.removeAdoption(adoptionId);
    }
}