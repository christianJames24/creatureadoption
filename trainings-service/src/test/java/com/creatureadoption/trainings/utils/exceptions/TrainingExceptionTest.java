package com.creatureadoption.trainings.utils.exceptions;

import com.creatureadoption.trainings.dataaccesslayer.Difficulty;
import com.creatureadoption.trainings.dataaccesslayer.TrainingCategory;
import com.creatureadoption.trainings.dataaccesslayer.TrainingStatus;
import com.creatureadoption.trainings.presentationlayer.TrainingRequestModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TrainingExceptionTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URI = "/api/v1/trainings";

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
    public void whenUpdateNonExistentTraining_thenThrowNotFoundException() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        TrainingRequestModel requestModel = buildTrainingRequest();
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
    public void whenDeleteNonExistentTraining_thenThrowNotFoundException() {
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
    public void whenGetAllTrainingsWithInvalidIdFormat_thenThrowInvalidInputException() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .queryParam("trainingId", "invalid-format")
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
        TrainingRequestModel requestModel = buildTrainingRequest();
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
    public void testNotFoundExceptionStructure() {
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
                .jsonPath("$.path").isNotEmpty()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    public void whenAddingTrainingWithDuplicateName_thenThrowDuplicateTrainingNameException() {
        // arrange
        TrainingRequestModel requestModel = buildTrainingRequest();

        // Mock behavior to match service implementation: first POST succeeds, second fails
        // First POST to create the initial training
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isCreated();

        // Second POST with same name should fail with 422
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().contains("name");
                });
    }

    private TrainingRequestModel buildTrainingRequest() {
        //arrange
        return TrainingRequestModel.builder()
                .name("Advanced Fire Magic")
                .description("Master the art of controlling magical fire")
                .difficulty(Difficulty.ADVANCED)
                .duration(120)
                .status(TrainingStatus.ACTIVE)
                .category(TrainingCategory.ATTACK)
                .price(299.99)
                .location("Fire Temple")
                .build();
    }
}
