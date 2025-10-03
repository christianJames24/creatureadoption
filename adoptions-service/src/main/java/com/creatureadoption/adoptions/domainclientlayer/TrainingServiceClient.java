package com.creatureadoption.adoptions.domainclientlayer;

import com.creatureadoption.adoptions.domainclientlayer.models.TrainingResponseModel;
import com.creatureadoption.adoptions.utils.HttpErrorInfo;
import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class TrainingServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String TRAININGS_SERVICE_BASE_URL;

    public TrainingServiceClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.trainings-service.host}") String trainingsServiceHost,
            @Value("${app.trainings-service.port}") String trainingsServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.TRAININGS_SERVICE_BASE_URL = "http://" + trainingsServiceHost + ":" + trainingsServicePort + "/api/v1/trainings";
    }

    public TrainingResponseModel getTrainingByTrainingId(String trainingId) {
        try {
            String url = TRAININGS_SERVICE_BASE_URL + "/" + trainingId;
            log.debug("Calling Trainings-Service URL: {}", url);

            return restTemplate.getForObject(url, TrainingResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        if (ex.getStatusCode() == NOT_FOUND) {
            return new NotFoundException(getErrorMessage(ex));
        }
        if (ex.getStatusCode() == UNPROCESSABLE_ENTITY) {
            return new InvalidInputException(getErrorMessage(ex));
        }
//        if (ex.getStatusCode() == FORBIDDEN) {
//            return new AdoptionLimitExceededException(getErrorMessage(ex));
//        }
        log.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        log.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ioex.getMessage();
        }
    }
}