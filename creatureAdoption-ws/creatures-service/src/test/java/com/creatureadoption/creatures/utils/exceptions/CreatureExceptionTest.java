package com.creatureadoption.creatures.utils.exceptions;

import com.creatureadoption.creatures.dataaccesslayer.CreatureStatus;
import com.creatureadoption.creatures.dataaccesslayer.CreatureType;
import com.creatureadoption.creatures.dataaccesslayer.Rarity;
import com.creatureadoption.creatures.dataaccesslayer.Temperament;
import com.creatureadoption.creatures.presentationlayer.CreatureRequestModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CreatureExceptionTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final String BASE_URI = "/api/v1/creatures";

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
    public void whenUpdateNonExistentCreature_thenThrowNotFoundException() {
        //arrange
        String nonExistentId = UUID.randomUUID().toString();
        CreatureRequestModel requestModel = CreatureRequestModel.builder().build();
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
    public void whenDeleteNonExistentCreature_thenThrowNotFoundException() {
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
    public void whenAddingCreatureWithDuplicateName_thenThrowDuplicateCreatureNameException() {
        CreatureRequestModel requestModel = CreatureRequestModel.builder()
                .name("Duplicate Test Creature")
                .species("Test Species")
                .type(CreatureType.NORMAL)
                .rarity(Rarity.COMMON)
                .level(1)
                .age(1)
                .health(100)
                .experience(0)
                .status(CreatureStatus.AVAILABLE)
                .strength(10)
                .intelligence(10)
                .agility(10)
                .temperament(Temperament.FRIENDLY)
                .build();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isCreated();

        webTestClient.post()
                .uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestModel)
                .exchange()
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY)
                .expectBody()
                .jsonPath("$.message").isNotEmpty()
                .jsonPath("$.message").value(message -> {
                    assert message.toString().toLowerCase().contains("already exists") ||
                            message.toString().toLowerCase().contains("duplicate");
                });
    }
}
