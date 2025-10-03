package com.creatureadoption.apigateway.presentationlayer.creatures;

import com.creatureadoption.apigateway.businesslayer.creatures.CreaturesService;
import com.creatureadoption.apigateway.domainclientlayer.creatures.*;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import com.creatureadoption.apigateway.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CreaturesController.class)
class CreaturesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreaturesService creaturesService;

    private final String CREATURE_ID = "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String INVALID_CREATURE_ID = "invalid-id";
    private CreatureResponseModel creatureResponseModel;
    private CreatureRequestModel creatureRequestModel;
    private List<CreatureResponseModel> creatureResponseModels;

    @BeforeEach
    void setUp() {
        creatureResponseModel = new CreatureResponseModel(
                CREATURE_ID,
                "REG-12345",
                "Sparky",
                "Dragon",
                CreatureType.FIRE,
                Rarity.RARE,
                10,
                5,
                100,
                500,
                CreatureStatus.AVAILABLE,
                80,
                70,
                90,
                Temperament.FRIENDLY
        );

        creatureRequestModel = CreatureRequestModel.builder()
                .name("Sparky")
                .species("Dragon")
                .type(CreatureType.FIRE)
                .rarity(Rarity.RARE)
                .level(10)
                .age(5)
                .health(100)
                .experience(500)
                .status(CreatureStatus.AVAILABLE)
                .strength(80)
                .intelligence(70)
                .agility(90)
                .temperament(Temperament.FRIENDLY)
                .build();

        creatureResponseModels = Collections.singletonList(creatureResponseModel);
    }

//    @Test
//    void getCreatures_ShouldReturnAllCreatures() throws Exception {
//        when(creaturesService.getCreatures(any(Map.class))).thenReturn(creatureResponseModels);
//
//        mockMvc.perform(get("/api/v1/creatures")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].creatureId", is(CREATURE_ID)))
//                .andExpect(jsonPath("$[0].name", is("Sparky")))
//                .andExpect(jsonPath("$[0].species", is("Dragon")))
//                .andExpect(jsonPath("$[0]._links.self.href").exists())
//                .andExpect(jsonPath("$[0]._links.allCreatures.href").exists());
//
//        verify(creaturesService, times(1)).getCreatures(any(Map.class));
//    }

    @Test
    void getCreatures_WithValidCreatureId_ShouldReturnFilteredCreatures() throws Exception {
        Map<String, String> queryParams = Collections.singletonMap("creatureId", CREATURE_ID);
        when(creaturesService.getCreatures(eq(queryParams))).thenReturn(creatureResponseModels);

        mockMvc.perform(get("/api/v1/creatures")
                        .param("creatureId", CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].creatureId", is(CREATURE_ID)));

        verify(creaturesService, times(1)).getCreatures(eq(queryParams));
    }

    @Test
    void getCreatures_WithInvalidCreatureId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/creatures")
                        .param("creatureId", INVALID_CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(creaturesService, never()).getCreatures(any(Map.class));
    }

    @Test
    void getCreatureByCreatureId_WithValidId_ShouldReturnCreature() throws Exception {
        when(creaturesService.getCreatureByCreatureId(CREATURE_ID)).thenReturn(creatureResponseModel);

        mockMvc.perform(get("/api/v1/creatures/{creatureId}", CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creatureId", is(CREATURE_ID)))
                .andExpect(jsonPath("$.name", is("Sparky")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allCreatures.href").exists());

        verify(creaturesService, times(1)).getCreatureByCreatureId(CREATURE_ID);
    }

    @Test
    void getCreatureByCreatureId_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/creatures/{creatureId}", INVALID_CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(creaturesService, never()).getCreatureByCreatureId(anyString());
    }

    @Test
    void getCreatureByCreatureId_CreatureNotFound_ShouldThrowNotFoundException() throws Exception {
        when(creaturesService.getCreatureByCreatureId(CREATURE_ID)).thenThrow(new NotFoundException("Creature not found"));

        mockMvc.perform(get("/api/v1/creatures/{creatureId}", CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(creaturesService, times(1)).getCreatureByCreatureId(CREATURE_ID);
    }

    @Test
    void addCreature_ShouldCreateCreature() throws Exception {
        when(creaturesService.addCreature(any(CreatureRequestModel.class))).thenReturn(creatureResponseModel);

        mockMvc.perform(post("/api/v1/creatures")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sparky\",\"species\":\"Dragon\",\"type\":\"FIRE\",\"rarity\":\"RARE\",\"level\":10,\"age\":5,\"health\":100,\"experience\":500,\"status\":\"AVAILABLE\",\"strength\":80,\"intelligence\":70,\"agility\":90,\"temperament\":\"FRIENDLY\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.creatureId", is(CREATURE_ID)))
                .andExpect(jsonPath("$.name", is("Sparky")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allCreatures.href").exists());

        verify(creaturesService, times(1)).addCreature(any(CreatureRequestModel.class));
    }

    @Test
    void updateCreature_WithValidId_ShouldUpdateCreature() throws Exception {
        when(creaturesService.updateCreature(any(CreatureRequestModel.class), eq(CREATURE_ID))).thenReturn(creatureResponseModel);

        mockMvc.perform(put("/api/v1/creatures/{creatureId}", CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sparky\",\"species\":\"Dragon\",\"type\":\"FIRE\",\"rarity\":\"RARE\",\"level\":10,\"age\":5,\"health\":100,\"experience\":500,\"status\":\"AVAILABLE\",\"strength\":80,\"intelligence\":70,\"agility\":90,\"temperament\":\"FRIENDLY\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creatureId", is(CREATURE_ID)))
                .andExpect(jsonPath("$.name", is("Sparky")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allCreatures.href").exists());

        verify(creaturesService, times(1)).updateCreature(any(CreatureRequestModel.class), eq(CREATURE_ID));
    }

    @Test
    void updateCreature_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(put("/api/v1/creatures/{creatureId}", INVALID_CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sparky\",\"species\":\"Dragon\",\"type\":\"FIRE\",\"rarity\":\"RARE\",\"level\":10,\"age\":5,\"health\":100,\"experience\":500,\"status\":\"AVAILABLE\",\"strength\":80,\"intelligence\":70,\"agility\":90,\"temperament\":\"FRIENDLY\"}"))
                .andExpect(status().isUnprocessableEntity());

        verify(creaturesService, never()).updateCreature(any(CreatureRequestModel.class), anyString());
    }

    @Test
    void updateCreature_CreatureNotFound_ShouldThrowNotFoundException() throws Exception {
        when(creaturesService.updateCreature(any(CreatureRequestModel.class), eq(CREATURE_ID)))
                .thenThrow(new NotFoundException("Creature not found"));

        mockMvc.perform(put("/api/v1/creatures/{creatureId}", CREATURE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Sparky\",\"species\":\"Dragon\",\"type\":\"FIRE\",\"rarity\":\"RARE\",\"level\":10,\"age\":5,\"health\":100,\"experience\":500,\"status\":\"AVAILABLE\",\"strength\":80,\"intelligence\":70,\"agility\":90,\"temperament\":\"FRIENDLY\"}"))
                .andExpect(status().isNotFound());

        verify(creaturesService, times(1)).updateCreature(any(CreatureRequestModel.class), eq(CREATURE_ID));
    }

    @Test
    void deleteCreature_WithValidId_ShouldDeleteCreature() throws Exception {
        doNothing().when(creaturesService).removeCreature(CREATURE_ID);

        mockMvc.perform(delete("/api/v1/creatures/{creatureId}", CREATURE_ID))
                .andExpect(status().isNoContent());

        verify(creaturesService, times(1)).removeCreature(CREATURE_ID);
    }

    @Test
    void deleteCreature_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/v1/creatures/{creatureId}", INVALID_CREATURE_ID))
                .andExpect(status().isUnprocessableEntity());

        verify(creaturesService, never()).removeCreature(anyString());
    }

    @Test
    void deleteCreature_CreatureNotFound_ShouldThrowNotFoundException() throws Exception {
        doThrow(new NotFoundException("Creature not found")).when(creaturesService).removeCreature(CREATURE_ID);

        mockMvc.perform(delete("/api/v1/creatures/{creatureId}", CREATURE_ID))
                .andExpect(status().isNotFound());

        verify(creaturesService, times(1)).removeCreature(CREATURE_ID);
    }
}