package com.creatureadoption.apigateway.domainclientlayer.creatures;

import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureRequestModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureResponseModel;
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
public class CreaturesServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String CREATURES_SERVICE_BASE_URL;

    public CreaturesServiceClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.creatures-service.host}") String creaturesServiceHost,
            @Value("${app.creatures-service.port}") String creaturesServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.CREATURES_SERVICE_BASE_URL = "http://" + creaturesServiceHost + ":" + creaturesServicePort + "/api/v1/creatures";
    }

    public List<CreatureResponseModel> getCreatures(Map<String, String> queryParams) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(CREATURES_SERVICE_BASE_URL);
            queryParams.forEach(builder::queryParam);

            String url = builder.build().toUriString();
            log.debug("Calling Creatures-Service URL: {}", url);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CreatureResponseModel>>() {}).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public CreatureResponseModel getCreatureByCreatureId(String creatureId) {
        try {
            String url = CREATURES_SERVICE_BASE_URL + "/" + creatureId;
            log.debug("Calling Creatures-Service URL: {}", url);

            return restTemplate.getForObject(url, CreatureResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public CreatureResponseModel addCreature(CreatureRequestModel creatureRequestModel) {
        try {
            String url = CREATURES_SERVICE_BASE_URL;
            log.debug("Calling Creatures-Service URL: {}", url);

            return restTemplate.postForObject(url, creatureRequestModel, CreatureResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public CreatureResponseModel updateCreature(CreatureRequestModel creatureRequestModel, String creatureId) {
        try {
            String url = CREATURES_SERVICE_BASE_URL + "/" + creatureId;
            log.debug("Calling Creatures-Service URL: {}", url);

            HttpEntity<CreatureRequestModel> requestEntity = new HttpEntity<>(creatureRequestModel);
            return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, CreatureResponseModel.class).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void removeCreature(String creatureId) {
        try {
            String url = CREATURES_SERVICE_BASE_URL + "/" + creatureId;
            log.debug("Calling Creatures-Service URL: {}", url);

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