package com.creatureadoption.trainings.presentationlayer;

import com.creatureadoption.trainings.dataaccesslayer.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql({"/data-h2.sql"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TrainingControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private TrainingRepository trainingRepository;

    private static final String BASE_URI = "/api/v1/trainings";

    @Test
    public void whenGetAllTrainings_thenReturnAllTrainings() {
        //arrange
        //act
        webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TrainingResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    assertThat(list.size()).isGreaterThan(0);
                });
    }

    @Test
    public void whenGetTrainingsWithDifficultyFilter_thenReturnFilteredTrainings() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .queryParam("difficulty", "BEGINNER")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TrainingResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    list.forEach(training ->
                            assertThat(training.getDifficulty()).isEqualTo(Difficulty.BEGINNER));
                });
    }

    @Test
    public void whenGetTrainingsWithCategoryFilter_thenReturnFilteredTrainings() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .queryParam("category", "ATTACK")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TrainingResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    list.forEach(training ->
                            assertThat(training.getCategory()).isEqualTo(TrainingCategory.ATTACK));
                });
    }

    @Test
    public void whenGetTrainingsWithStatusFilter_thenReturnFilteredTrainings() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .queryParam("status", "ACTIVE")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(TrainingResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    list.forEach(training ->
                            assertThat(training.getStatus()).isEqualTo(TrainingStatus.ACTIVE));
                });
    }

    @Test
    public void whenGetTrainingsWithInvalidTrainingIdParam_thenReturnUnprocessable() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URI)
                        .queryParam("trainingId", "invalid-id")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
    }

    @Test
    public void whenGetTrainingByValidId_thenReturnTraining() {
        //arrange
        Training training = trainingRepository.findAll().get(0);
        String trainingId = training.getTrainingIdentifier().getTrainingId();
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + trainingId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.trainingId").isEqualTo(trainingId);
    }

    @Test
    public void whenGetTrainingByInvalidId_thenReturnNotFound() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + nonExistentId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isNotFound();
    }

    @Test
    public void whenGetTrainingWithInvalidIdFormat_thenReturnUnprocessable() {
        //arrange
        String invalidId = "invalid-id";
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + invalidId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
    }

    @Test
    public void whenCreateTraining_thenReturnCreatedTraining() {
        //arrange
        TrainingRequestModel request = buildTrainingRequest();
        //act
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(request.getName())
                .jsonPath("$.description").isEqualTo(request.getDescription());
    }

    @Test
    public void whenCreateTraining_thenCheckAllFieldsInResponse() {
        //arrange
        TrainingRequestModel request = buildTrainingRequest();
        //act
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.trainingId").exists()
                .jsonPath("$.trainingCode").exists()
                .jsonPath("$.name").isEqualTo(request.getName())
                .jsonPath("$.description").isEqualTo(request.getDescription())
                .jsonPath("$.difficulty").isEqualTo(request.getDifficulty().toString())
                .jsonPath("$.duration").isEqualTo(request.getDuration())
                .jsonPath("$.status").isEqualTo(request.getStatus().toString())
                .jsonPath("$.category").isEqualTo(request.getCategory().toString())
                .jsonPath("$.price").isEqualTo(request.getPrice())
                .jsonPath("$.location").isEqualTo(request.getLocation())
                .jsonPath("$._links.self.href").exists()
                .jsonPath("$._links.allTrainings.href").exists();
    }

    @Test
    public void whenCreateTrainingWithDifferentValues_thenReturnExpectedResponse() {
        //arrange
        TrainingRequestModel request = TrainingRequestModel.builder()
                .name("Contest Training")
                .description("Learn how to prepare for magical creature contests")
                .difficulty(Difficulty.INTERMEDIATE)
                .duration(90)
                .status(TrainingStatus.ACTIVE)
                .category(TrainingCategory.CONTEST)
                .price(149.99)
                .location("Contest Academy")
                .build();
        //act
        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(request.getName())
                .jsonPath("$.description").isEqualTo(request.getDescription())
                .jsonPath("$.difficulty").isEqualTo(request.getDifficulty().toString())
                .jsonPath("$.category").isEqualTo(request.getCategory().toString())
                .jsonPath("$.price").isEqualTo(request.getPrice())
                .jsonPath("$.location").isEqualTo(request.getLocation());
    }

    @Test
    public void whenUpdateTraining_thenReturnUpdatedTraining() {
        //arrange
        Training training = trainingRepository.findAll().get(0);
        String trainingId = training.getTrainingIdentifier().getTrainingId();
        TrainingRequestModel request = buildTrainingRequest();
        //act
        webTestClient.put()
                .uri(BASE_URI + "/" + trainingId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(request.getName())
                .jsonPath("$.trainingId").isEqualTo(trainingId);
    }

    @Test
    public void whenUpdateWithInvalidIdFormat_thenReturnUnprocessable() {
        //arrange
        String invalidId = "invalid-id";
        TrainingRequestModel request = buildTrainingRequest();
        //act
        webTestClient.put()
                .uri(BASE_URI + "/" + invalidId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
    }

    @Test
    public void whenUpdateWithNonExistentId_thenReturnNotFound() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        TrainingRequestModel request = buildTrainingRequest();
        //act
        webTestClient.put()
                .uri(BASE_URI + "/" + nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isNotFound();
    }

    @Test
    public void whenDeleteTraining_thenReturnNoContent() {
        //arrange
        Training training = trainingRepository.findAll().get(0);
        String trainingId = training.getTrainingIdentifier().getTrainingId();
        //act
        webTestClient.delete()
                .uri(BASE_URI + "/" + trainingId)
                .exchange()
                //assert
                .expectStatus().isNoContent();
        //arrange
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + trainingId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isNotFound();
    }

    @Test
    public void whenDeleteWithInvalidIdFormat_thenReturnUnprocessable() {
        //arrange
        String invalidId = "invalid-id";
        //act
        webTestClient.delete()
                .uri(BASE_URI + "/" + invalidId)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
    }

    @Test
    public void whenDeleteWithNonExistentId_thenReturnNotFound() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        //act
        webTestClient.delete()
                .uri(BASE_URI + "/" + nonExistentId)
                .exchange()
                //assert
                .expectStatus().isNotFound();
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
