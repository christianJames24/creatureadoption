package com.creatureadoption.creatures.presentationlayer;

import com.creatureadoption.creatures.dataaccesslayer.Creature;
import com.creatureadoption.creatures.dataaccesslayer.CreatureRepository;
import com.creatureadoption.creatures.dataaccesslayer.CreatureType;
import com.creatureadoption.creatures.dataaccesslayer.Rarity;
import com.creatureadoption.creatures.dataaccesslayer.CreatureStatus;
import com.creatureadoption.creatures.dataaccesslayer.Temperament;
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
public class CreatureControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CreatureRepository creatureRepository;

    private static final String BASE_URI = "/api/v1/creatures";

    @Test
    public void whenGetAllCreatures_thenReturnAllCreatures() {
        //arrange
        //act
        webTestClient.get()
                .uri(BASE_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CreatureResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    assertThat(list.size()).isGreaterThan(0);
                });
    }

    @Test
    public void whenGetCreaturesWithTypeFilter_thenReturnFilteredCreatures() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URI).queryParam("type", "FIRE").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CreatureResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    assertThat(list).allMatch(creature -> creature.getType() == CreatureType.FIRE);
                });
    }

    @Test
    public void whenGetCreaturesWithRarityFilter_thenReturnFilteredCreatures() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URI).queryParam("rarity", "COMMON").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CreatureResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    assertThat(list).allMatch(creature -> creature.getRarity() == Rarity.COMMON);
                });
    }

    @Test
    public void whenGetCreaturesWithStatusFilter_thenReturnFilteredCreatures() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URI).queryParam("status", "AVAILABLE").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(CreatureResponseModel.class)
                .value(list -> {
                    assertThat(list).isNotNull();
                    assertThat(list).allMatch(creature -> creature.getStatus() == CreatureStatus.AVAILABLE);
                });
    }

    @Test
    public void whenGetCreaturesWithInvalidCreatureIdParam_thenReturnUnprocessableEntity() {
        //arrange
        //act
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_URI).queryParam("creatureId", "invalid-id").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isEqualTo(UNPROCESSABLE_ENTITY);
    }

    @Test
    public void whenGetCreatureByValidId_thenReturnCreature() {
        //arrange
        Creature creature = creatureRepository.findAll().get(0);
        String creatureId = creature.getCreatureIdentifier().getCreatureId();
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + creatureId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.creatureId").isEqualTo(creatureId);
    }

    @Test
    public void whenGetCreatureByInvalidId_thenReturnNotFound() {
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
    public void whenGetCreatureWithInvalidIdFormat_thenReturnUnprocessable() {
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
    public void whenCreateCreature_thenReturnCreatedCreature() {
        //arrange
        CreatureRequestModel request = buildCreatureRequest();
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
                .jsonPath("$.species").isEqualTo(request.getSpecies());
    }

    @Test
    public void whenCreateCreature_thenCheckAllFieldsInResponse() {
        //arrange
        CreatureRequestModel request = buildCreatureRequest();
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
                .jsonPath("$.creatureId").exists()
                .jsonPath("$.registrationCode").exists()
                .jsonPath("$.name").isEqualTo(request.getName())
                .jsonPath("$.species").isEqualTo(request.getSpecies())
                .jsonPath("$.type").isEqualTo(request.getType().toString())
                .jsonPath("$.rarity").isEqualTo(request.getRarity().toString())
                .jsonPath("$.level").isEqualTo(request.getLevel())
                .jsonPath("$.age").isEqualTo(request.getAge())
                .jsonPath("$.health").isEqualTo(request.getHealth())
                .jsonPath("$.experience").isEqualTo(request.getExperience())
                .jsonPath("$.status").isEqualTo(request.getStatus().toString())
                .jsonPath("$.strength").isEqualTo(request.getStrength())
                .jsonPath("$.intelligence").isEqualTo(request.getIntelligence())
                .jsonPath("$.agility").isEqualTo(request.getAgility())
                .jsonPath("$.temperament").isEqualTo(request.getTemperament().toString())
                .jsonPath("$._links.self.href").exists()
                .jsonPath("$._links.allCreatures.href").exists();
    }

    @Test
    public void whenCreateCreatureWithDifferentValues_thenReturnExpectedResponse() {
        //arrange
        CreatureRequestModel request = CreatureRequestModel.builder()
                .name("Water Elemental")
                .species("Elementalis Aqua")
                .type(CreatureType.WATER)
                .rarity(Rarity.UNCOMMON)
                .level(5)
                .age(20)
                .health(200)
                .experience(150)
                .status(CreatureStatus.AVAILABLE)
                .strength(30)
                .intelligence(85)
                .agility(70)
                .temperament(Temperament.FRIENDLY)
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
                .jsonPath("$.species").isEqualTo(request.getSpecies())
                .jsonPath("$.type").isEqualTo(request.getType().toString())
                .jsonPath("$.rarity").isEqualTo(request.getRarity().toString())
                .jsonPath("$.level").isEqualTo(request.getLevel())
                .jsonPath("$.status").isEqualTo(request.getStatus().toString())
                .jsonPath("$.temperament").isEqualTo(request.getTemperament().toString());
    }

    @Test
    public void whenUpdateCreature_thenReturnUpdatedCreature() {
        //arrange
        Creature creature = creatureRepository.findAll().get(0);
        String creatureId = creature.getCreatureIdentifier().getCreatureId();
        CreatureRequestModel request = buildCreatureRequest();
        //act
        webTestClient.put()
                .uri(BASE_URI + "/" + creatureId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //assert
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo(request.getName())
                .jsonPath("$.creatureId").isEqualTo(creatureId);
    }

    @Test
    public void whenUpdateWithInvalidIdFormat_thenReturnUnprocessable() {
        //arrange
        String invalidId = "invalid-id";
        CreatureRequestModel request = buildCreatureRequest();
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
        CreatureRequestModel request = buildCreatureRequest();
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
    public void whenDeleteCreature_thenReturnNoContent() {
        //arrange
        Creature creature = creatureRepository.findAll().get(0);
        String creatureId = creature.getCreatureIdentifier().getCreatureId();
        //act
        webTestClient.delete()
                .uri(BASE_URI + "/" + creatureId)
                .exchange()
                //assert
                .expectStatus().isNoContent();
        //arrange
        //act
        webTestClient.get()
                .uri(BASE_URI + "/" + creatureId)
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

    private CreatureRequestModel buildCreatureRequest() {
        return CreatureRequestModel.builder()
                .name("Magic Dragon")
                .species("Draconis Arcanum")
                .type(CreatureType.DRAGON)
                .rarity(Rarity.LEGENDARY)
                .level(10)
                .age(150)
                .health(500)
                .experience(1000)
                .status(CreatureStatus.AVAILABLE)
                .strength(95)
                .intelligence(90)
                .agility(80)
                .temperament(Temperament.AGGRESSIVE)
                .build();
    }
}
