package com.creatureadoption.adoptions.domainclientlayer;

import com.creatureadoption.adoptions.dataaccesslayer.CreatureStatus;
import com.creatureadoption.adoptions.domainclientlayer.models.CreatureRequestModel;
import com.creatureadoption.adoptions.domainclientlayer.models.CreatureResponseModel;
import com.creatureadoption.adoptions.utils.HttpErrorInfo;
import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class CreatureServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String CREATURES_SERVICE_BASE_URL;

    public CreatureServiceClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.creatures-service.host}") String creaturesServiceHost,
            @Value("${app.creatures-service.port}") String creaturesServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.CREATURES_SERVICE_BASE_URL = "http://" + creaturesServiceHost + ":" + creaturesServicePort + "/api/v1/creatures";
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

    public CreatureResponseModel updateCreatureStatus(String creatureId, CreatureStatus newStatus) {
        try {
            String url = CREATURES_SERVICE_BASE_URL + "/" + creatureId;
            log.debug("Calling Creatures-Service URL to update status: {}", url);

            // Get the existing creature info
            CreatureResponseModel existingCreature = getCreatureByCreatureId(creatureId);

            // Create a request with updated status
            CreatureRequestModel updateRequest = CreatureRequestModel.builder()
                    .name(existingCreature.getName())
                    .species(existingCreature.getSpecies())
                    .type(existingCreature.getType())
                    .rarity(existingCreature.getRarity())
                    .level(existingCreature.getLevel())
                    .age(existingCreature.getAge())
                    .health(existingCreature.getHealth())
                    .experience(existingCreature.getExperience())
                    .status(newStatus)
                    .strength(existingCreature.getStrength())
                    .intelligence(existingCreature.getIntelligence())
                    .agility(existingCreature.getAgility())
                    .temperament(existingCreature.getTemperament())
                    .build();

            HttpEntity<CreatureRequestModel> requestEntity = new HttpEntity<>(updateRequest);
            return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, CreatureResponseModel.class).getBody();
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
        if (ex.getStatusCode() == FORBIDDEN) {
            return new AdoptionLimitExceededException(getErrorMessage(ex));
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