package com.creatureadoption.adoptions.businesslayer;

import com.creatureadoption.adoptions.presentationlayer.AdoptionRequestModel;
import com.creatureadoption.adoptions.presentationlayer.AdoptionResponseModel;

import java.util.List;
import java.util.Map;

public interface AdoptionService {

    List<AdoptionResponseModel> getAdoptions(Map<String, String> queryParams);
    AdoptionResponseModel getAdoptionByAdoptionId(String adoptionId);
    AdoptionResponseModel addAdoption(AdoptionRequestModel adoptionRequestModel);
    AdoptionResponseModel updateAdoption(AdoptionRequestModel updatedAdoption, String adoptionId);
    AdoptionResponseModel updateAdoptionStatus(String adoptionId, String newStatus);
    void removeAdoption(String adoptionId);
}