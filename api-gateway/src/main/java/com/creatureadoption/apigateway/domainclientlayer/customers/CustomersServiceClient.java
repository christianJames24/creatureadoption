package com.creatureadoption.apigateway.domainclientlayer.customers;

import com.creatureadoption.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerResponseModel;
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
public class CustomersServiceClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final String CUSTOMERS_SERVICE_BASE_URL;

    public CustomersServiceClient(
            RestTemplate restTemplate,
            ObjectMapper mapper,
            @Value("${app.customers-service.host}") String customersServiceHost,
            @Value("${app.customers-service.port}") String customersServicePort) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.CUSTOMERS_SERVICE_BASE_URL = "http://" + customersServiceHost + ":" + customersServicePort + "/api/v1/customers";
    }

    public List<CustomerResponseModel> getCustomers(Map<String, String> queryParams) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(CUSTOMERS_SERVICE_BASE_URL);
            queryParams.forEach(builder::queryParam);

            String url = builder.build().toUriString();
            log.debug("Calling Customers-Service URL: {}", url);

            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CustomerResponseModel>>() {}).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public CustomerResponseModel getCustomerByCustomerId(String customerId) {
        try {
            String url = CUSTOMERS_SERVICE_BASE_URL + "/" + customerId;
            log.debug("Calling Customers-Service URL: {}", url);

            return restTemplate.getForObject(url, CustomerResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        try {
            String url = CUSTOMERS_SERVICE_BASE_URL;
            log.debug("Calling Customers-Service URL: {}", url);

            return restTemplate.postForObject(url, customerRequestModel, CustomerResponseModel.class);
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public CustomerResponseModel updateCustomer(CustomerRequestModel customerRequestModel, String customerId) {
        try {
            String url = CUSTOMERS_SERVICE_BASE_URL + "/" + customerId;
            log.debug("Calling Customers-Service URL: {}", url);

            HttpEntity<CustomerRequestModel> requestEntity = new HttpEntity<>(customerRequestModel);
            return restTemplate.exchange(url, HttpMethod.PUT, requestEntity, CustomerResponseModel.class).getBody();
        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public void removeCustomer(String customerId) {
        try {
            String url = CUSTOMERS_SERVICE_BASE_URL + "/" + customerId;
            log.debug("Calling Customers-Service URL: {}", url);

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