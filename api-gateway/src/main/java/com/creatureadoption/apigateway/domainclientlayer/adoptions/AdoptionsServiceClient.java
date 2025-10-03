package com.creatureadoption.apigateway.domainclientlayer.adoptions;

import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionRequestModel;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.utils.HttpErrorInfo;
import com.creatureadoption.apigateway.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import com.creatureadoption.apigateway.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
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
public class AdoptionsServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String ADOPTIONS_SERVICE_BASE_URL;

    public AdoptionsServiceClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.adoptions-service.host}") String adoptionsServiceHost,
            @Value("${app.adoptions-service.port}") String adoptionsServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.ADOPTIONS_SERVICE_BASE_URL = "http://" + adoptionsServiceHost + ":" + adoptionsServicePort + "/api/v1/adoptions";
    }

    public List<AdoptionResponseModel> getAdoptions(Map<String, String> queryParams) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(ADOPTIONS_SERVICE_BASE_URL);
            queryParams.forEach(builder::queryParam);

            String url = builder.build().toUriString();
            log.debug("Calling Adoptions-Service URL: {}", url);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AdoptionResponseModel>>() {}).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdoptionResponseModel getAdoptionByAdoptionId(String adoptionId) {
        try {
            String url = ADOPTIONS_SERVICE_BASE_URL + "/" + adoptionId;
            log.debug("Calling Adoptions-Service URL: {}", url);

            return restTemplate.getForObject(url, AdoptionResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdoptionResponseModel addAdoption(AdoptionRequestModel adoptionRequestModel) {
        try {
            String url = ADOPTIONS_SERVICE_BASE_URL;
            log.debug("Calling Adoptions-Service URL: {}", url);

            return restTemplate.postForObject(url, adoptionRequestModel, AdoptionResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdoptionResponseModel updateAdoption(AdoptionRequestModel adoptionRequestModel, String adoptionId) {
        try {
            String url = ADOPTIONS_SERVICE_BASE_URL + "/" + adoptionId;
            log.debug("Calling Adoptions-Service URL: {}", url);

            HttpEntity<AdoptionRequestModel> requestEntity = new HttpEntity<>(adoptionRequestModel);
            return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, AdoptionResponseModel.class).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public AdoptionResponseModel updateAdoptionStatus(String adoptionId, String status) {
        try {
            String url = ADOPTIONS_SERVICE_BASE_URL + "/" + adoptionId + "/status/" + status;
            log.debug("Calling Adoptions-Service URL: {}", url);

            return restTemplate.exchange(url, HttpMethod.PATCH, null, AdoptionResponseModel.class).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void removeAdoption(String adoptionId) {
        try {
            String url = ADOPTIONS_SERVICE_BASE_URL + "/" + adoptionId;
            log.debug("Calling Adoptions-Service URL: {}", url);

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
//        if (ex.getStatusCode() == HttpStatus.FORBIDDEN) {
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