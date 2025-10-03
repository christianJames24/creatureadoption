package com.creatureadoption.apigateway.domainclientlayer.creatures;

import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureRequestModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureResponseModel;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CreaturesServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private CreaturesServiceClient creaturesServiceClient;

    private final String CREATURE_ID = "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String BASE_URL = "http://creatures-service:8080/api/v1/creatures";
    private CreatureResponseModel creatureResponseModel;
    private CreatureRequestModel creatureRequestModel;
    private List<CreatureResponseModel> creatureResponseModels;
    private Map<String, String> queryParams;
    private HttpErrorInfo errorInfo;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Using reflection to set the BASE_URL field
        java.lang.reflect.Field field = CreaturesServiceClient.class.getDeclaredField("CREATURES_SERVICE_BASE_URL");
        field.setAccessible(true);
        field.set(creaturesServiceClient, BASE_URL);

        creatureResponseModel = new CreatureResponseModel(
                CREATURE_ID,
                "REG-12345",
                "Sparky",
                "Dragon",
                CreatureType.FIRE,
                Rarity.RARE,
                10,
                5,
                100,
                500,
                CreatureStatus.AVAILABLE,
                80,
                70,
                90,
                Temperament.FRIENDLY
        );

        creatureRequestModel = CreatureRequestModel.builder()
                .name("Sparky")
                .species("Dragon")
                .type(CreatureType.FIRE)
                .rarity(Rarity.RARE)
                .level(10)
                .age(5)
                .health(100)
                .experience(500)
                .status(CreatureStatus.AVAILABLE)
                .strength(80)
                .intelligence(70)
                .agility(90)
                .temperament(Temperament.FRIENDLY)
                .build();

        creatureResponseModels = Collections.singletonList(creatureResponseModel);
        queryParams = new HashMap<>();
        errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/creatures/123", "Creature not found");
    }

    @Test
    void getCreatures_ShouldReturnCreatures() {
        ResponseEntity<List<CreatureResponseModel>> responseEntity = new ResponseEntity<>(creatureResponseModels, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<CreatureResponseModel> result = creaturesServiceClient.getCreatures(queryParams);

        assertEquals(creatureResponseModels, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getCreatureByCreatureId_ShouldReturnCreature() {
        when(restTemplate.getForObject(BASE_URL + "/" + CREATURE_ID, CreatureResponseModel.class))
                .thenReturn(creatureResponseModel);

        CreatureResponseModel result = creaturesServiceClient.getCreatureByCreatureId(CREATURE_ID);

        assertEquals(creatureResponseModel, result);
        verify(restTemplate, times(1)).getForObject(
                eq(BASE_URL + "/" + CREATURE_ID),
                eq(CreatureResponseModel.class)
        );
    }

    @Test
    void addCreature_ShouldReturnCreatedCreature() {
        when(restTemplate.postForObject(BASE_URL, creatureRequestModel, CreatureResponseModel.class))
                .thenReturn(creatureResponseModel);

        CreatureResponseModel result = creaturesServiceClient.addCreature(creatureRequestModel);

        assertEquals(creatureResponseModel, result);
        verify(restTemplate, times(1)).postForObject(
                eq(BASE_URL),
                eq(creatureRequestModel),
                eq(CreatureResponseModel.class)
        );
    }

    @Test
    void updateCreature_ShouldReturnUpdatedCreature() {
        ResponseEntity<CreatureResponseModel> responseEntity = new ResponseEntity<>(creatureResponseModel, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(BASE_URL + "/" + CREATURE_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(CreatureResponseModel.class)
        )).thenReturn(responseEntity);

        CreatureResponseModel result = creaturesServiceClient.updateCreature(creatureRequestModel, CREATURE_ID);

        assertEquals(creatureResponseModel, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL + "/" + CREATURE_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(CreatureResponseModel.class)
        );
    }

    @Test
    void removeCreature_ShouldCallRestTemplate() {
        doNothing().when(restTemplate).delete(BASE_URL + "/" + CREATURE_ID);

        creaturesServiceClient.removeCreature(CREATURE_ID);

        verify(restTemplate, times(1)).delete(eq(BASE_URL + "/" + CREATURE_ID));
    }

    @Test
    void handleHttpClientException_NotFound_ShouldThrowNotFoundException() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(CreatureResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            creaturesServiceClient.getCreatureByCreatureId("non-existent-id");
        });

        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
    }

    @Test
    void handleHttpClientException_UnprocessableEntity_ShouldThrowInvalidInputException() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(CreatureResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);

        Exception exception = assertThrows(InvalidInputException.class, () -> {
            creaturesServiceClient.getCreatureByCreatureId("invalid-id");
        });

        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
    }

    @Test
    void handleHttpClientException_OtherStatus_ShouldRethrowException() {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(CreatureResponseModel.class))).thenThrow(ex);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            creaturesServiceClient.getCreatureByCreatureId(CREATURE_ID);
        });

        assertEquals(ex, exception);
    }
}