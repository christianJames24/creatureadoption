package com.creatureadoption.apigateway.domainclientlayer.customers;

import com.creatureadoption.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerResponseModel;
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

class CustomersServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private CustomersServiceClient customersServiceClient;

    private final String CUSTOMER_ID = "f2f7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String BASE_URL = "http://customers-service:8080/api/v1/customers";
    private CustomerResponseModel customerResponseModel;
    private CustomerRequestModel customerRequestModel;
    private List<CustomerResponseModel> customerResponseModels;
    private Map<String, String> queryParams;
    private HttpErrorInfo errorInfo;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Using reflection to set the BASE_URL field
        java.lang.reflect.Field field = CustomersServiceClient.class.getDeclaredField("CUSTOMERS_SERVICE_BASE_URL");
        field.setAccessible(true);
        field.set(customersServiceClient, BASE_URL);

        PhoneNumber phoneNumber = new PhoneNumber(PhoneType.MOBILE, "123-456-7890");
        List<PhoneNumber> phoneNumbers = Collections.singletonList(phoneNumber);

        customerResponseModel = new CustomerResponseModel(
                CUSTOMER_ID,
                "John",
                "Doe",
                "john.doe@example.com",
                ContactMethodPreference.EMAIL,
                "123 Main St",
                "City",
                "Province",
                "Country",
                "12345",
                phoneNumbers
        );

        customerRequestModel = CustomerRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailAddress("john.doe@example.com")
                .contactMethodPreference(ContactMethodPreference.EMAIL)
                .streetAddress("123 Main St")
                .city("City")
                .province("Province")
                .country("Country")
                .postalCode("12345")
                .phoneNumbers(phoneNumbers)
                .build();

        customerResponseModels = Collections.singletonList(customerResponseModel);
        queryParams = new HashMap<>();
        errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/api/v1/customers/123", "Customer not found");
    }

    @Test
    void getCustomers_ShouldReturnCustomers() {
        ResponseEntity<List<CustomerResponseModel>> responseEntity = new ResponseEntity<>(customerResponseModels, HttpStatus.OK);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        )).thenReturn(responseEntity);

        List<CustomerResponseModel> result = customersServiceClient.getCustomers(queryParams);

        assertEquals(customerResponseModels, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL),
                eq(HttpMethod.GET),
                eq(null),
                any(ParameterizedTypeReference.class)
        );
    }

    @Test
    void getCustomerByCustomerId_ShouldReturnCustomer() {
        when(restTemplate.getForObject(BASE_URL + "/" + CUSTOMER_ID, CustomerResponseModel.class))
                .thenReturn(customerResponseModel);

        CustomerResponseModel result = customersServiceClient.getCustomerByCustomerId(CUSTOMER_ID);

        assertEquals(customerResponseModel, result);
        verify(restTemplate, times(1)).getForObject(
                eq(BASE_URL + "/" + CUSTOMER_ID),
                eq(CustomerResponseModel.class)
        );
    }

    @Test
    void addCustomer_ShouldReturnCreatedCustomer() {
        when(restTemplate.postForObject(BASE_URL, customerRequestModel, CustomerResponseModel.class))
                .thenReturn(customerResponseModel);

        CustomerResponseModel result = customersServiceClient.addCustomer(customerRequestModel);

        assertEquals(customerResponseModel, result);
        verify(restTemplate, times(1)).postForObject(
                eq(BASE_URL),
                eq(customerRequestModel),
                eq(CustomerResponseModel.class)
        );
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() {
        ResponseEntity<CustomerResponseModel> responseEntity = new ResponseEntity<>(customerResponseModel, HttpStatus.OK);
        when(restTemplate.exchange(
                eq(BASE_URL + "/" + CUSTOMER_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(CustomerResponseModel.class)
        )).thenReturn(responseEntity);

        CustomerResponseModel result = customersServiceClient.updateCustomer(customerRequestModel, CUSTOMER_ID);

        assertEquals(customerResponseModel, result);
        verify(restTemplate, times(1)).exchange(
                eq(BASE_URL + "/" + CUSTOMER_ID),
                eq(HttpMethod.PUT),
                any(HttpEntity.class),
                eq(CustomerResponseModel.class)
        );
    }

    @Test
    void removeCustomer_ShouldCallRestTemplate() {
        doNothing().when(restTemplate).delete(BASE_URL + "/" + CUSTOMER_ID);

        customersServiceClient.removeCustomer(CUSTOMER_ID);

        verify(restTemplate, times(1)).delete(eq(BASE_URL + "/" + CUSTOMER_ID));
    }

//    @Test
//    void handleHttpClientException_NotFound_ShouldThrowNotFoundException() throws JsonProcessingException {
//        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null);
//        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);
//
//        Exception exception = assertThrows(NotFoundException.class, () -> {
//            customersServiceClient.getCustomerByCustomerId("non-existent-id");
//        });
//
//        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
//    }
//
//    @Test
//    void handleHttpClientException_UnprocessableEntity_ShouldThrowInvalidInputException() throws JsonProcessingException {
//        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable Entity", null, null, null);
//        when(mapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);
//
//        Exception exception = assertThrows(InvalidInputException.class, () -> {
//            customersServiceClient.getCustomerByCustomerId("invalid-id");
//        });
//
//        verify(mapper, times(1)).readValue(anyString(), eq(HttpErrorInfo.class));
//    }

    @Test
    void handleHttpClientException_OtherStatus_ShouldRethrowException() {
        HttpClientErrorException ex = HttpClientErrorException.create(HttpStatus.BAD_REQUEST, "Bad Request", null, null, null);
        when(restTemplate.getForObject(anyString(), eq(CustomerResponseModel.class))).thenThrow(ex);

        Exception exception = assertThrows(HttpClientErrorException.class, () -> {
            customersServiceClient.getCustomerByCustomerId(CUSTOMER_ID);
        });

        assertEquals(ex, exception);
    }
}