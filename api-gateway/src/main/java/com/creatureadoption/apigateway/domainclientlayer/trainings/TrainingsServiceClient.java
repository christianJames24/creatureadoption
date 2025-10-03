package com.creatureadoption.apigateway.domainclientlayer.trainings;

import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingRequestModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingResponseModel;
import com.creatureadoption.apigateway.utils.HttpErrorInfo;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import com.creatureadoption.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Component
public class TrainingsServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String TRAININGS_SERVICE_BASE_URL;

    public TrainingsServiceClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.trainings-service.host}") String trainingsServiceHost,
            @Value("${app.trainings-service.port}") String trainingsServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.TRAININGS_SERVICE_BASE_URL = "http://" + trainingsServiceHost + ":" + trainingsServicePort + "/api/v1/trainings";
    }

    public List<TrainingResponseModel> getTrainings(Map<String, String> queryParams) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(TRAININGS_SERVICE_BASE_URL);
            queryParams.forEach(builder::queryParam);

            String url = builder.build().toUriString();
            log.debug("Calling Trainings-Service URL: {}", url);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<TrainingResponseModel>>() {}).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
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

    public TrainingResponseModel addTraining(TrainingRequestModel trainingRequestModel) {
        try {
            String url = TRAININGS_SERVICE_BASE_URL;
            log.debug("Calling Trainings-Service URL: {}", url);

            return restTemplate.postForObject(url, trainingRequestModel, TrainingResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public TrainingResponseModel updateTraining(TrainingRequestModel trainingRequestModel, String trainingId) {
        try {
            String url = TRAININGS_SERVICE_BASE_URL + "/" + trainingId;
            log.debug("Calling Trainings-Service URL: {}", url);

            HttpEntity<TrainingRequestModel> requestEntity = new HttpEntity<>(trainingRequestModel);
            return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, TrainingResponseModel.class).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void removeTraining(String trainingId) {
        try {
            String url = TRAININGS_SERVICE_BASE_URL + "/" + trainingId;
            log.debug("Calling Trainings-Service URL: {}", url);

            restTemplate.delete(url);
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