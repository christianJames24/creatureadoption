package com.creatureadoption.apigateway.domainclientlayer.trainings;

import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingRequestModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingResponseModel;
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

class TrainingsServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private TrainingsServiceClient trainingsServiceClient;

    private final String TRAINING_ID = "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String BASE_URL = "http://trainings-service:8080/api/v1/trainings";
    private TrainingResponseModel trainingResponseModel;
    private TrainingRequestModel trainingRequestModel;
    private List<TrainingResponseModel> trainingResponseModels;
    private Map<String, String> queryParams;
    private HttpErrorInfo errorInfo;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Using reflection to set the BASE_URL field
        java.lang.reflect.Field field = TrainingsServiceClient.class.getDeclaredField("TRAININGS_SERVICE_BASE_URL");
        field.setAccessible(true);
        field.set(trainingsServiceClient, BASE_URL);

        trainingResponseModel = new TrainingResponseModel(
                TRAINING_ID,
                "TR-12345",
                "Fire Breathing",
                "Learn to breathe fire",
                Difficulty.ADVANCED,
                60,
                TrainingStatus.ACTIVE,
                TrainingCategory.ATTACK,
                99.99,
                "Volcano Academy"
        );

        trainingRequestModel = TrainingRequestModel.builder()
                .name("Fire Breathing")
                .description("Learn to breathe fire")
                .difficulty(Difficulty.ADVANCED)
                .duration(60)
                .status(TrainingStatus.ACTIVE)
                .category(TrainingCategory.ATTACK)
                .price(99.99)
                .location("Volcano Academy")
                .build();

        trainingResponseModels = Collections.singletonList(trainingResponseModel);
        queryParams = new HashMap<>();
        errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/trainings/123", "Training not found");
    }

    @Test
    void getTrainings_ShouldReturnTrainings() {
        ResponseEntity<List<TrainingResponseModel>> responseEntity = new ResponseEntity<>(trainingResponseModels, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<TrainingResponseModel> result = trainingsServiceClient.getTrainings(queryParams);

        assertEquals(trainingResponseModels, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getTrainingByTrainingId_ShouldReturnTraining() {
        when(restTemplate.getForObject(BASE_URL + "/" + TRAINING_ID, TrainingResponseModel.class))
                .thenReturn(trainingResponseModel);

        TrainingResponseModel result = trainingsServiceClient.getTrainingByTrainingId(TRAINING_ID);

        assertEquals(trainingResponseModel, result);
        verify(restTemplate, times(1)).getForObject(
                eq(BASE_URL + "/" + TRAINING_ID),
                eq(TrainingResponseModel.class)
        );
    }

    @Test
    void addTraining_ShouldReturnCreatedTraining() {
        when(restTemplate.postForObject(BASE_URL, trainingRequestModel, TrainingResponseModel.class))
                .thenReturn(trainingResponseModel);

        TrainingResponseModel result = trainingsServiceClient.addTraining(trainingRequestModel);

        assertEquals(trainingResponseModel, result);
        verify(restTemplate, times(1)).postForObject(
                eq(BASE_URL),
                eq(trainingRequestModel),
                eq(TrainingResponseModel.class)
        );
    }

    @Test
    void updateTraining_ShouldReturnUpdatedTraining() {
        ResponseEntity<TrainingResponseModel> responseEntity = new ResponseEntity<>(trainingResponseModel, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(BASE_URL + "/" + TRAINING_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(TrainingResponseModel.class)
        )).thenReturn(responseEntity);

        TrainingResponseModel result = trainingsServiceClient.updateTraining(trainingRequestModel, TRAINING_ID);

        assertEquals(trainingResponseModel, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL + "/" + TRAINING_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(TrainingResponseModel.class)
        );
    }

    @Test
    void removeTraining_ShouldCallRestTemplate() {
        doNothing().when(restTemplate).delete(BASE_URL + "/" + TRAINING_ID);

        trainingsServiceClient.removeTraining(TRAINING_ID);

        verify(restTemplate, times(1)).delete(eq(BASE_URL + "/" + TRAINING_ID));
    }

    @Test
    void handleHttpClientException_NotFound_ShouldThrowNotFoundException() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(TrainingResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);

        Exception exception = assertThrows(NotFoundException.class, () -> {
            trainingsServiceClient.getTrainingByTrainingId("non-existent-id");
        });

        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
    }

    @Test
    void handleHttpClientException_UnprocessableEntity_ShouldThrowInvalidInputException() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(TrainingResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);

        Exception exception = assertThrows(InvalidInputException.class, () -> {
            trainingsServiceClient.getTrainingByTrainingId("invalid-id");
        });

        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
    }

    @Test
    void handleHttpClientException_OtherStatus_ShouldRethrowException() {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(TrainingResponseModel.class))).thenThrow(ex);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            trainingsServiceClient.getTrainingByTrainingId(TRAINING_ID);
        });

        assertEquals(ex, exception);
    }

    @Test
    void handleHttpClientException_JsonProcessingException_ShouldHandleCorrectly() throws JsonProcessingException {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(TrainingResponseModel.class))).thenThrow(ex);
        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenThrow(new JsonProcessingException("Error parsing JSON") {});

        Exception exception = assertThrows(NotFoundException.class, () -> {
            trainingsServiceClient.getTrainingByTrainingId("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Error parsing JSON"));
    }
}