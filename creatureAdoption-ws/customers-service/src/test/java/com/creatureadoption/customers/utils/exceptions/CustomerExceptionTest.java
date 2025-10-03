package com.creatureadoption.customers.utils.exceptions;

import com.creatureadoption.customers.dataaccesslayer.ContactMethodPreference;
import com.creatureadoption.customers.dataaccesslayer.PhoneNumber;
import com.creatureadoption.customers.dataaccesslayer.PhoneType;
import com.creatureadoption.customers.presentationlayer.CustomerRequestModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/schema-mysql.sql", "/data-mysql.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerExceptionTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URI = "/api/v1/customers";

    @Test
    public void whenInvalidUUIDFormat_thenThrowInvalidInputException() {
        //arrange
        //act
        webTestClient.get()
                .uri(BASE_URI + "/invalid-uuid")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("Invalid");
                });
    }

    @Test
    public void whenNonExistentUUID_thenThrowNotFoundException() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("not found");
                });
    }

    @Test
    public void whenUpdateNonExistentCustomer_thenThrowNotFoundException() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        CustomerRequestModel requestModel = buildCustomerRequest();
        //act
        webTestClient.put()
                .uri(BASE_URI + "/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                //assert
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("not found");
                });
    }

    @Test
    public void whenDeleteNonExistentCustomer_thenThrowNotFoundException() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        //act
        webTestClient.delete()
                .uri(BASE_URI + "/" + nonExistentId)
                .exchange()
                //assert
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("not found");
                });
    }

    @Test
    public void whenGetAllCustomersWithInvalidIdFormat_thenThrowInvalidInputException() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .queryParam("customerId", "invalid-format")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("Invalid");
                });
    }

    @Test
    public void whenPutWithInvalidIdFormat_thenThrowInvalidInputException() {
        //arrange
        CustomerRequestModel requestModel = buildCustomerRequest();
        //act
        webTestClient.put()
                .uri(BASE_URI + "/invalid-format")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("Invalid");
                });
    }

    @Test
    public void whenDeleteWithInvalidIdFormat_thenThrowInvalidInputException() {
        //arrange
        //act
        webTestClient.delete()
                .uri(BASE_URI + "/invalid-format")
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("Invalid");
                });
    }

    @Test
    public void testGlobalExceptionHandling() {
        //arrange
        String invalidId = "invalid-id";
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.path").isNotEmpty()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    public void whenDeleteWithRequestBody_thenThrowDeleteRequestBodyNotAllowedException() {
        // arrange
        String validId = UUID.randomUUID().toString();
        CustomerRequestModel body = buildCustomerRequest();

        // act & assert
        webTestClient.method(HttpMethod.DELETE)
                .uri(BASE_URI + "/" + validId)
                .contentType(MediaType.APPLICATION_JSON)   // now available
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("DELETE must not include a request body");
    }




    @Test
    public void whenAddingCustomerWithDuplicateEmail_thenThrowDuplicateEmailException() {
        // arrange
        CustomerRequestModel requestModel = buildCustomerRequest();

        // Mock behavior to match service implementation: first POST succeeds, second fails
        // First POST to create the initial customer
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isCreated();

        // Second POST with same email should fail with 422
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("email");
                });
    }


    private CustomerRequestModel buildCustomerRequest() {
        //arrange
        PhoneNumber phone = new PhoneNumber(PhoneType.MOBILE, "1234567890");
        List<PhoneNumber> phones = new ArrayList<>();
        phones.add(phone);
        //act
        return CustomerRequestModel.builder()
                .firstName("Test")
                .lastName("Customer")
                .emailAddress("test@example.com")
                .contactMethodPreference(ContactMethodPreference.EMAIL)
                .streetAddress("123 Test St")
                .city("Test City")
                .province("Test Province")
                .country("Test Country")
                .postalCode("12345")
                .phoneNumbers(phones)
                .build();
    }
}
