package com.creatureadoption.apigateway.presentationlayer.trainings;

import com.creatureadoption.apigateway.businesslayer.trainings.TrainingsService;
import com.creatureadoption.apigateway.domainclientlayer.trainings.Difficulty;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingCategory;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingStatus;
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

@WebMvcTest(TrainingsController.class)
class TrainingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingsService trainingsService;

    private final String TRAINING_ID = "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String INVALID_TRAINING_ID = "invalid-id";
    private TrainingResponseModel trainingResponseModel;
    private TrainingRequestModel trainingRequestModel;
    private List<TrainingResponseModel> trainingResponseModels;

    @BeforeEach
    void setUp() {
        trainingResponseModel = new TrainingResponseModel(
                TRAINING_ID,
                "TR-12345",
                "Fire Breathing",
                "Learn to breathe fire",
                Difficulty.ADVANCED,
                60,
                TrainingStatus.ACTIVE,
                TrainingCategory.ATTACK,
                99.99,
                "Volcano Academy"
        );

        trainingRequestModel = TrainingRequestModel.builder()
                .name("Fire Breathing")
                .description("Learn to breathe fire")
                .difficulty(Difficulty.ADVANCED)
                .duration(60)
                .status(TrainingStatus.ACTIVE)
                .category(TrainingCategory.ATTACK)
                .price(99.99)
                .location("Volcano Academy")
                .build();

        trainingResponseModels = Collections.singletonList(trainingResponseModel);
    }

//    @Test
//    void getTrainings_ShouldReturnAllTrainings() throws Exception {
//        when(trainingsService.getTrainings(any(Map.class))).thenReturn(trainingResponseModels);
//
//        mockMvc.perform(get("/api/v1/trainings")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].trainingId", is(TRAINING_ID)))
//                .andExpect(jsonPath("$[0].name", is("Fire Breathing")))
//                .andExpect(jsonPath("$[0].description", is("Learn to breathe fire")))
//                .andExpect(jsonPath("$[0]._links.self.href").exists())
//                .andExpect(jsonPath("$[0]._links.allTrainings.href").exists());
//
//        verify(trainingsService, times(1)).getTrainings(any(Map.class));
//    }

    @Test
    void getTrainings_WithValidTrainingId_ShouldReturnFilteredTrainings() throws Exception {
        Map<String, String> queryParams = Collections.singletonMap("trainingId", TRAINING_ID);
        when(trainingsService.getTrainings(eq(queryParams))).thenReturn(trainingResponseModels);

        mockMvc.perform(get("/api/v1/trainings")
                        .param("trainingId", TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trainingId", is(TRAINING_ID)));

        verify(trainingsService, times(1)).getTrainings(eq(queryParams));
    }

    @Test
    void getTrainings_WithInvalidTrainingId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/trainings")
                        .param("trainingId", INVALID_TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(trainingsService, never()).getTrainings(any(Map.class));
    }

    @Test
    void getTrainingByTrainingId_WithValidId_ShouldReturnTraining() throws Exception {
        when(trainingsService.getTrainingByTrainingId(TRAINING_ID)).thenReturn(trainingResponseModel);

        mockMvc.perform(get("/api/v1/trainings/{trainingId}", TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingId", is(TRAINING_ID)))
                .andExpect(jsonPath("$.name", is("Fire Breathing")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allTrainings.href").exists());

        verify(trainingsService, times(1)).getTrainingByTrainingId(TRAINING_ID);
    }

    @Test
    void getTrainingByTrainingId_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/trainings/{trainingId}", INVALID_TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(trainingsService, never()).getTrainingByTrainingId(anyString());
    }

    @Test
    void getTrainingByTrainingId_TrainingNotFound_ShouldThrowNotFoundException() throws Exception {
        when(trainingsService.getTrainingByTrainingId(TRAINING_ID)).thenThrow(new NotFoundException("Training not found"));

        mockMvc.perform(get("/api/v1/trainings/{trainingId}", TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(trainingsService, times(1)).getTrainingByTrainingId(TRAINING_ID);
    }

    @Test
    void addTraining_ShouldCreateTraining() throws Exception {
        when(trainingsService.addTraining(any(TrainingRequestModel.class))).thenReturn(trainingResponseModel);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fire Breathing\",\"description\":\"Learn to breathe fire\",\"difficulty\":\"ADVANCED\",\"duration\":60,\"status\":\"ACTIVE\",\"category\":\"ATTACK\",\"price\":99.99,\"location\":\"Volcano Academy\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainingId", is(TRAINING_ID)))
                .andExpect(jsonPath("$.name", is("Fire Breathing")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allTrainings.href").exists());

        verify(trainingsService, times(1)).addTraining(any(TrainingRequestModel.class));
    }

    @Test
    void updateTraining_WithValidId_ShouldUpdateTraining() throws Exception {
        when(trainingsService.updateTraining(any(TrainingRequestModel.class), eq(TRAINING_ID))).thenReturn(trainingResponseModel);

        mockMvc.perform(put("/api/v1/trainings/{trainingId}", TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fire Breathing\",\"description\":\"Learn to breathe fire\",\"difficulty\":\"ADVANCED\",\"duration\":60,\"status\":\"ACTIVE\",\"category\":\"ATTACK\",\"price\":99.99,\"location\":\"Volcano Academy\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingId", is(TRAINING_ID)))
                .andExpect(jsonPath("$.name", is("Fire Breathing")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allTrainings.href").exists());

        verify(trainingsService, times(1)).updateTraining(any(TrainingRequestModel.class), eq(TRAINING_ID));
    }

    @Test
    void updateTraining_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(put("/api/v1/trainings/{trainingId}", INVALID_TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fire Breathing\",\"description\":\"Learn to breathe fire\",\"difficulty\":\"ADVANCED\",\"duration\":60,\"status\":\"ACTIVE\",\"category\":\"ATTACK\",\"price\":99.99,\"location\":\"Volcano Academy\"}"))
                .andExpect(status().isUnprocessableEntity());

        verify(trainingsService, never()).updateTraining(any(TrainingRequestModel.class), anyString());
    }

    @Test
    void updateTraining_TrainingNotFound_ShouldThrowNotFoundException() throws Exception {
        when(trainingsService.updateTraining(any(TrainingRequestModel.class), eq(TRAINING_ID)))
                .thenThrow(new NotFoundException("Training not found"));

        mockMvc.perform(put("/api/v1/trainings/{trainingId}", TRAINING_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Fire Breathing\",\"description\":\"Learn to breathe fire\",\"difficulty\":\"ADVANCED\",\"duration\":60,\"status\":\"ACTIVE\",\"category\":\"ATTACK\",\"price\":99.99,\"location\":\"Volcano Academy\"}"))
                .andExpect(status().isNotFound());

        verify(trainingsService, times(1)).updateTraining(any(TrainingRequestModel.class), eq(TRAINING_ID));
    }

    @Test
    void deleteTraining_WithValidId_ShouldDeleteTraining() throws Exception {
        doNothing().when(trainingsService).removeTraining(TRAINING_ID);

        mockMvc.perform(delete("/api/v1/trainings/{trainingId}", TRAINING_ID))
                .andExpect(status().isNoContent());

        verify(trainingsService, times(1)).removeTraining(TRAINING_ID);
    }

    @Test
    void deleteTraining_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/v1/trainings/{trainingId}", INVALID_TRAINING_ID))
                .andExpect(status().isUnprocessableEntity());

        verify(trainingsService, never()).removeTraining(anyString());
    }

    @Test
    void deleteTraining_TrainingNotFound_ShouldThrowNotFoundException() throws Exception {
        doThrow(new NotFoundException("Training not found")).when(trainingsService).removeTraining(TRAINING_ID);

        mockMvc.perform(delete("/api/v1/trainings/{trainingId}", TRAINING_ID))
                .andExpect(status().isNotFound());

        verify(trainingsService, times(1)).removeTraining(TRAINING_ID);
    }
}