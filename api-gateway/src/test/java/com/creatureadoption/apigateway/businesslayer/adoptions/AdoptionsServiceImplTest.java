package com.creatureadoption.apigateway.businesslayer.adoptions;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.*;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionRequestModel;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AdoptionsServiceImplTest {

    @Mock
    private AdoptionsServiceClient adoptionsServiceClient;

    @InjectMocks
    private AdoptionsServiceImpl adoptionsService;

    private final String ADOPTION_ID = "a2a7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String CUSTOMER_ID = "c1c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String CREATURE_ID = "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String TRAINING_ID = "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";

    private AdoptionResponseModel adoptionResponseModel;
    private AdoptionRequestModel adoptionRequestModel;
    private List<AdoptionResponseModel> adoptionResponseModels;
    private Map<String, String> queryParams;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        LocalDate profileDate = LocalDate.of(2023, 1, 1);
        LocalDate adoptionDate = LocalDate.of(2023, 2, 1);
        LocalDateTime lastUpdated = LocalDateTime.of(2023, 2, 1, 12, 0);

        adoptionResponseModel = new AdoptionResponseModel(
                ADOPTION_ID,
                "ADO-12345",
                "First adoption",
                1,
                profileDate,
                lastUpdated,
                ProfileStatus.ACTIVE,
                adoptionDate,
                "Creature Adoption Center",
                AdoptionStatus.APPROVED,
                "Special notes for this adoption",
                CUSTOMER_ID,
                "John",
                "Doe",
                CREATURE_ID,
                "Sparky",
                "Dragon",
                CreatureStatus.ADOPTED,
                TRAINING_ID,
                "Fire Breathing",
                "Volcano Academy"
        );

        adoptionRequestModel = AdoptionRequestModel.builder()
                .summary("First adoption")
                .totalAdoptions(1)
                .profileCreationDate(profileDate)
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(adoptionDate)
                .adoptionLocation("Creature Adoption Center")
                .adoptionStatus(AdoptionStatus.APPROVED)
                .specialNotes("Special notes for this adoption")
                .customerId(CUSTOMER_ID)
                .creatureId(CREATURE_ID)
                .trainingId(TRAINING_ID)
                .build();

        adoptionResponseModels = Collections.singletonList(adoptionResponseModel);
        queryParams = new HashMap<>();
    }

    @Test
    void getAdoptions_ShouldCallClientAndReturnAdoptions() {
        when(adoptionsServiceClient.getAdoptions(queryParams)).thenReturn(adoptionResponseModels);

        List<AdoptionResponseModel> result = adoptionsService.getAdoptions(queryParams);

        assertEquals(adoptionResponseModels, result);
        verify(adoptionsServiceClient, times(1)).getAdoptions(queryParams);
    }

    @Test
    void getAdoptionByAdoptionId_ShouldCallClientAndReturnAdoption() {
        when(adoptionsServiceClient.getAdoptionByAdoptionId(ADOPTION_ID)).thenReturn(adoptionResponseModel);

        AdoptionResponseModel result = adoptionsService.getAdoptionByAdoptionId(ADOPTION_ID);

        assertEquals(adoptionResponseModel, result);
        verify(adoptionsServiceClient, times(1)).getAdoptionByAdoptionId(ADOPTION_ID);
    }

    @Test
    void addAdoption_ShouldCallClientAndReturnCreatedAdoption() {
        when(adoptionsServiceClient.addAdoption(adoptionRequestModel)).thenReturn(adoptionResponseModel);

        AdoptionResponseModel result = adoptionsService.addAdoption(adoptionRequestModel);

        assertEquals(adoptionResponseModel, result);
        verify(adoptionsServiceClient, times(1)).addAdoption(adoptionRequestModel);
    }

    @Test
    void updateAdoption_ShouldCallClientAndReturnUpdatedAdoption() {
        when(adoptionsServiceClient.updateAdoption(adoptionRequestModel, ADOPTION_ID)).thenReturn(adoptionResponseModel);

        AdoptionResponseModel result = adoptionsService.updateAdoption(adoptionRequestModel, ADOPTION_ID);

        assertEquals(adoptionResponseModel, result);
        verify(adoptionsServiceClient, times(1)).updateAdoption(adoptionRequestModel, ADOPTION_ID);
    }

    @Test
    void updateAdoptionStatus_ShouldCallClientAndReturnUpdatedAdoption() {
        when(adoptionsServiceClient.updateAdoptionStatus(ADOPTION_ID, "COMPLETED")).thenReturn(adoptionResponseModel);

        AdoptionResponseModel result = adoptionsService.updateAdoptionStatus(ADOPTION_ID, "COMPLETED");

        assertEquals(adoptionResponseModel, result);
        verify(adoptionsServiceClient, times(1)).updateAdoptionStatus(ADOPTION_ID, "COMPLETED");
    }

    @Test
    void removeAdoption_ShouldCallClient() {
        doNothing().when(adoptionsServiceClient).removeAdoption(ADOPTION_ID);

        adoptionsService.removeAdoption(ADOPTION_ID);

        verify(adoptionsServiceClient, times(1)).removeAdoption(ADOPTION_ID);
    }
}