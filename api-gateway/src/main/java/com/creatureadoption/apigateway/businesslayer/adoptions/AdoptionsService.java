package com.creatureadoption.apigateway.businesslayer.adoptions;

import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionRequestModel;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;

import java.util.List;
import java.util.Map;

public interface AdoptionsService {
    List<AdoptionResponseModel> getAdoptions(Map<String, String> queryParams);
    AdoptionResponseModel getAdoptionByAdoptionId(String adoptionId);
    AdoptionResponseModel addAdoption(AdoptionRequestModel adoptionRequestModel);
    AdoptionResponseModel updateAdoption(AdoptionRequestModel adoptionRequestModel, String adoptionId);
    AdoptionResponseModel updateAdoptionStatus(String adoptionId, String status);
    void removeAdoption(String adoptionId);
}