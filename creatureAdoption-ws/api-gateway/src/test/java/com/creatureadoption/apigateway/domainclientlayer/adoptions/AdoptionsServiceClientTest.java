package com.creatureadoption.apigateway.domainclientlayer.adoptions;

import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionRequestModel;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.utils.HttpErrorInfo;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import com.creatureadoption.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AdoptionsServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private AdoptionsServiceClient adoptionsServiceClient;

    private final String ADOPTION_ID = "a2a7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String CUSTOMER_ID = "c1c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String CREATURE_ID = "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String TRAINING_ID = "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String BASE_URL = "http://adoptions-service:8080/api/v1/adoptions";
    private AdoptionResponseModel adoptionResponseModel;
    private AdoptionRequestModel adoptionRequestModel;
    private List<AdoptionResponseModel> adoptionResponseModels;
    private Map<String, String> queryParams;
    private HttpErrorInfo errorInfo;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Using reflection to set the BASE_URL field
        java.lang.reflect.Field field = AdoptionsServiceClient.class.getDeclaredField("ADOPTIONS_SERVICE_BASE_URL");
        field.setAccessible(true);
        field.set(adoptionsServiceClient, BASE_URL);

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
        errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/adoptions/123", "Adoption not found");
    }

    @Test
    void getAdoptions_ShouldReturnAdoptions() {
        ResponseEntity<List<AdoptionResponseModel>> responseEntity = new ResponseEntity<>(adoptionResponseModels, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<AdoptionResponseModel> result = adoptionsServiceClient.getAdoptions(queryParams);

        assertEquals(adoptionResponseModels, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getAdoptionByAdoptionId_ShouldReturnAdoption() {
        when(restTemplate.getForObject(BASE_URL + "/" + ADOPTION_ID, AdoptionResponseModel.class))
                .thenReturn(adoptionResponseModel);

        AdoptionResponseModel result = adoptionsServiceClient.getAdoptionByAdoptionId(ADOPTION_ID);

        assertEquals(adoptionResponseModel, result);
        verify(restTemplate, times(1)).getForObject(
                eq(BASE_URL + "/" + ADOPTION_ID),
                eq(AdoptionResponseModel.class)
        );
    }

    @Test
    void addAdoption_ShouldReturnCreatedAdoption() {
        when(restTemplate.postForObject(BASE_URL, adoptionRequestModel, AdoptionResponseModel.class))
                .thenReturn(adoptionResponseModel);

        AdoptionResponseModel result = adoptionsServiceClient.addAdoption(adoptionRequestModel);

        assertEquals(adoptionResponseModel, result);
        verify(restTemplate, times(1)).postForObject(
                eq(BASE_URL),
                eq(adoptionRequestModel),
                eq(AdoptionResponseModel.class)
        );
    }

    @Test
    void updateAdoption_ShouldReturnUpdatedAdoption() {
        ResponseEntity<AdoptionResponseModel> responseEntity = new ResponseEntity<>(adoptionResponseModel, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(BASE_URL + "/" + ADOPTION_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(AdoptionResponseModel.class)
        )).thenReturn(responseEntity);

        AdoptionResponseModel result = adoptionsServiceClient.updateAdoption(adoptionRequestModel, ADOPTION_ID);

        assertEquals(adoptionResponseModel, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL + "/" + ADOPTION_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(AdoptionResponseModel.class)
        );
    }

    @Test
    void updateAdoptionStatus_ShouldReturnUpdatedAdoption() {
        ResponseEntity<AdoptionResponseModel> responseEntity = new ResponseEntity<>(adoptionResponseModel, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(BASE_URL + "/" + ADOPTION_ID + "/status/COMPLETED"),
                eq(HttpMethod.PATCH),
                eq(null),
                eq(AdoptionResponseModel.class)
        )).thenReturn(responseEntity);

        AdoptionResponseModel result = adoptionsServiceClient.updateAdoptionStatus(ADOPTION_ID, "COMPLETED");

        assertEquals(adoptionResponseModel, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL + "/" + ADOPTION_ID + "/status/COMPLETED"),
                eq(HttpMethod.PATCH),
                eq(null),
                eq(AdoptionResponseModel.class)
        );
    }

    @Test
    void removeAdoption_ShouldCallRestTemplate() {
        doNothing().when(restTemplate).delete(BASE_URL + "/" + ADOPTION_ID);

        adoptionsServiceClient.removeAdoption(ADOPTION_ID);

        verify(restTemplate, times(1)).delete(eq(BASE_URL + "/" + ADOPTION_ID));
    }

    @Test
    void handleHttpClientException_NotFound_ShouldThrowNotFoundException() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdoptionResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            adoptionsServiceClient.getAdoptionByAdoptionId("non-existent-id");
        });

        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
    }

    @Test
    void handleHttpClientException_UnprocessableEntity_ShouldThrowInvalidInputException() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdoptionResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);

        Exception exception = assertThrows(InvalidInputException.class, () -> {
            adoptionsServiceClient.getAdoptionByAdoptionId("invalid-id");
        });

        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
    }

    @Test
    void handleHttpClientException_OtherStatus_ShouldRethrowException() {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdoptionResponseModel.class))).thenThrow(ex);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            adoptionsServiceClient.getAdoptionByAdoptionId(ADOPTION_ID);
        });

        assertEquals(ex, exception);
    }

    @Test
    void handleHttpClientException_JsonProcessingException_ShouldHandleCorrectly() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(AdoptionResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenThrow(new JsonProcessingException("Error parsing JSON") {});

        Exception exception = assertThrows(NotFoundException.class, () -> {
            adoptionsServiceClient.getAdoptionByAdoptionId("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Error parsing JSON"));
    }
}