package com.creatureadoption.apigateway.presentationlayer.adoptions;

import com.creatureadoption.apigateway.businesslayer.adoptions.AdoptionsService;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionStatus;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.ProfileStatus;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import com.creatureadoption.apigateway.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdoptionsController.class)
class AdoptionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdoptionsService adoptionsService;

    private final String ADOPTION_ID = "a2a7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String INVALID_ADOPTION_ID = "invalid-id";
    private final String CUSTOMER_ID = "c1c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String INVALID_CUSTOMER_ID = "invalid-customer-id";
    private final String CREATURE_ID = "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String INVALID_CREATURE_ID = "invalid-creature-id";
    private final String TRAINING_ID = "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";

    private AdoptionResponseModel adoptionResponseModel;
    private AdoptionRequestModel adoptionRequestModel;
    private List<AdoptionResponseModel> adoptionResponseModels;

    @BeforeEach
    void setUp() {
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
    }

//    @Test
//    void getAdoptions_ShouldReturnAllAdoptions() throws Exception {
//        when(adoptionsService.getAdoptions(any(Map.class))).thenReturn(adoptionResponseModels);
//
//        mockMvc.perform(get("/api/v1/adoptions")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].adoptionId", is(ADOPTION_ID)))
//                .andExpect(jsonPath("$[0].summary", is("First adoption")))
//                .andExpect(jsonPath("$[0]._links.self.href").exists())
//                .andExpect(jsonPath("$[0]._links.allAdoptions.href").exists());
//
//        verify(adoptionsService, times(1)).getAdoptions(any(Map.class));
//    }

    @Test
    void getAdoptions_WithValidAdoptionId_ShouldReturnFilteredAdoptions() throws Exception {
        Map<String, String> queryParams = Collections.singletonMap("adoptionId", ADOPTION_ID);
        when(adoptionsService.getAdoptions(eq(queryParams))).thenReturn(adoptionResponseModels);

        mockMvc.perform(get("/api/v1/adoptions")
                        .param("adoptionId", ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].adoptionId", is(ADOPTION_ID)));

        verify(adoptionsService, times(1)).getAdoptions(eq(queryParams));
    }

    @Test
    void getAdoptions_WithInvalidAdoptionId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("adoptionId", INVALID_ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).getAdoptions(any(Map.class));
    }

    @Test
    void getAdoptions_WithValidCustomerId_ShouldReturnFilteredAdoptions() throws Exception {
        Map<String, String> queryParams = Collections.singletonMap("customerId", CUSTOMER_ID);
        when(adoptionsService.getAdoptions(eq(queryParams))).thenReturn(adoptionResponseModels);

        mockMvc.perform(get("/api/v1/adoptions")
                        .param("customerId", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId", is(CUSTOMER_ID)));

        verify(adoptionsService, times(1)).getAdoptions(eq(queryParams));
    }

    @Test
    void getAdoptions_WithInvalidCustomerId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("customerId", INVALID_CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).getAdoptions(any(Map.class));
    }

    @Test
    void getAdoptions_WithValidCreatureId_ShouldReturnFilteredAdoptions() throws Exception {
        Map<String, String> queryParams = Collections.singletonMap("creatureId", CREATURE_ID);
        when(adoptionsService.getAdoptions(eq(queryParams))).thenReturn(adoptionResponseModels);

        mockMvc.perform(get("/api/v1/adoptions")
                        .param("creatureId", CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].creatureId", is(CREATURE_ID)));

        verify(adoptionsService, times(1)).getAdoptions(eq(queryParams));
    }

    @Test
    void getAdoptions_WithInvalidCreatureId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("creatureId", INVALID_CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).getAdoptions(any(Map.class));
    }

    @Test
    void getAdoptionByAdoptionId_WithValidId_ShouldReturnAdoption() throws Exception {
        when(adoptionsService.getAdoptionByAdoptionId(ADOPTION_ID)).thenReturn(adoptionResponseModel);

        mockMvc.perform(get("/api/v1/adoptions/{adoptionId}", ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adoptionId", is(ADOPTION_ID)))
                .andExpect(jsonPath("$.summary", is("First adoption")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allAdoptions.href").exists());

        verify(adoptionsService, times(1)).getAdoptionByAdoptionId(ADOPTION_ID);
    }

    @Test
    void getAdoptionByAdoptionId_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/adoptions/{adoptionId}", INVALID_ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).getAdoptionByAdoptionId(anyString());
    }

    @Test
    void getAdoptionByAdoptionId_AdoptionNotFound_ShouldThrowNotFoundException() throws Exception {
        when(adoptionsService.getAdoptionByAdoptionId(ADOPTION_ID)).thenThrow(new NotFoundException("Adoption not found"));

        mockMvc.perform(get("/api/v1/adoptions/{adoptionId}", ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(adoptionsService, times(1)).getAdoptionByAdoptionId(ADOPTION_ID);
    }

    @Test
    void addAdoption_ShouldCreateAdoption() throws Exception {
        when(adoptionsService.addAdoption(any(AdoptionRequestModel.class))).thenReturn(adoptionResponseModel);

        mockMvc.perform(post("/api/v1/adoptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"summary\":\"First adoption\",\"totalAdoptions\":1,\"profileCreationDate\":\"2023-01-01\",\"profileStatus\":\"ACTIVE\",\"adoptionDate\":\"2023-02-01\",\"adoptionLocation\":\"Creature Adoption Center\",\"adoptionStatus\":\"APPROVED\",\"specialNotes\":\"Special notes for this adoption\",\"customerId\":\"" + CUSTOMER_ID + "\",\"creatureId\":\"" + CREATURE_ID + "\",\"trainingId\":\"" + TRAINING_ID + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.adoptionId", is(ADOPTION_ID)))
                .andExpect(jsonPath("$.summary", is("First adoption")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allAdoptions.href").exists());

        verify(adoptionsService, times(1)).addAdoption(any(AdoptionRequestModel.class));
    }

    @Test
    void updateAdoption_WithValidId_ShouldUpdateAdoption() throws Exception {
        when(adoptionsService.updateAdoption(any(AdoptionRequestModel.class), eq(ADOPTION_ID))).thenReturn(adoptionResponseModel);

        mockMvc.perform(put("/api/v1/adoptions/{adoptionId}", ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"summary\":\"First adoption\",\"totalAdoptions\":1,\"profileCreationDate\":\"2023-01-01\",\"profileStatus\":\"ACTIVE\",\"adoptionDate\":\"2023-02-01\",\"adoptionLocation\":\"Creature Adoption Center\",\"adoptionStatus\":\"APPROVED\",\"specialNotes\":\"Special notes for this adoption\",\"customerId\":\"" + CUSTOMER_ID + "\",\"creatureId\":\"" + CREATURE_ID + "\",\"trainingId\":\"" + TRAINING_ID + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adoptionId", is(ADOPTION_ID)))
                .andExpect(jsonPath("$.summary", is("First adoption")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allAdoptions.href").exists());

        verify(adoptionsService, times(1)).updateAdoption(any(AdoptionRequestModel.class), eq(ADOPTION_ID));
    }

    @Test
    void updateAdoption_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(put("/api/v1/adoptions/{adoptionId}", INVALID_ADOPTION_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"summary\":\"First adoption\",\"totalAdoptions\":1,\"profileCreationDate\":\"2023-01-01\",\"profileStatus\":\"ACTIVE\",\"adoptionDate\":\"2023-02-01\",\"adoptionLocation\":\"Creature Adoption Center\",\"adoptionStatus\":\"APPROVED\",\"specialNotes\":\"Special notes for this adoption\",\"customerId\":\"" + CUSTOMER_ID + "\",\"creatureId\":\"" + CREATURE_ID + "\",\"trainingId\":\"" + TRAINING_ID + "\"}"))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).updateAdoption(any(AdoptionRequestModel.class), anyString());
    }

    @Test
    void updateAdoptionStatus_WithValidIdAndStatus_ShouldUpdateStatus() throws Exception {
        when(adoptionsService.updateAdoptionStatus(eq(ADOPTION_ID), eq("COMPLETED"))).thenReturn(adoptionResponseModel);

        mockMvc.perform(patch("/api/v1/adoptions/{adoptionId}/status/{status}", ADOPTION_ID, "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.adoptionId", is(ADOPTION_ID)))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allAdoptions.href").exists());

        verify(adoptionsService, times(1)).updateAdoptionStatus(eq(ADOPTION_ID), eq("COMPLETED"));
    }

    @Test
    void updateAdoptionStatus_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(patch("/api/v1/adoptions/{adoptionId}/status/{status}", INVALID_ADOPTION_ID, "COMPLETED")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).updateAdoptionStatus(anyString(), anyString());
    }

    @Test
    void deleteAdoption_WithValidId_ShouldDeleteAdoption() throws Exception {
        doNothing().when(adoptionsService).removeAdoption(ADOPTION_ID);

        mockMvc.perform(delete("/api/v1/adoptions/{adoptionId}", ADOPTION_ID))
                .andExpect(status().isNoContent());

        verify(adoptionsService, times(1)).removeAdoption(ADOPTION_ID);
    }

    @Test
    void deleteAdoption_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/v1/adoptions/{adoptionId}", INVALID_ADOPTION_ID))
                .andExpect(status().isUnprocessableEntity());

        verify(adoptionsService, never()).removeAdoption(anyString());
    }
}