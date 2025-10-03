package com.creatureadoption.apigateway.businesslayer.trainings;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionStatus;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.ProfileStatus;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.domainclientlayer.trainings.*;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingRequestModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingResponseModel;
import com.creatureadoption.apigateway.utils.exceptions.EntityInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingsServiceImplTest {

    @Mock
    private TrainingsServiceClient trainingsServiceClient;

    @Mock
    private AdoptionsServiceClient adoptionsServiceClient;

    @InjectMocks
    private TrainingsServiceImpl trainingsService;

    private final String TRAINING_ID = "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private TrainingResponseModel trainingResponseModel;
    private TrainingRequestModel trainingRequestModel;
    private AdoptionResponseModel adoptionResponseModel;
    private List<TrainingResponseModel> trainingResponseModels;
    private Map<String, String> queryParams;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

        adoptionResponseModel = new AdoptionResponseModel(
                "a2a7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "ADO-12345",
                "Training adoption",
                1,
                LocalDate.now(),
                LocalDateTime.now(),
                ProfileStatus.ACTIVE,
                LocalDate.now(),
                "Creature Adoption Center",
                AdoptionStatus.APPROVED,
                "Notes",
                "c1c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "John",
                "Doe",
                "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "Sparky",
                "Dragon",
                CreatureStatus.ADOPTED,
                TRAINING_ID,  // Use the test class's training ID
                "Fire Training",
                "Academy"
        );

        trainingResponseModels = Collections.singletonList(trainingResponseModel);
        queryParams = new HashMap<>();
    }

    @Test
    void getTrainings_ShouldCallClientAndReturnTrainings() {
        when(trainingsServiceClient.getTrainings(queryParams)).thenReturn(trainingResponseModels);

        List<TrainingResponseModel> result = trainingsService.getTrainings(queryParams);

        assertEquals(trainingResponseModels, result);
        verify(trainingsServiceClient, times(1)).getTrainings(queryParams);
    }

    @Test
    void getTrainingByTrainingId_ShouldCallClientAndReturnTraining() {
        when(trainingsServiceClient.getTrainingByTrainingId(TRAINING_ID)).thenReturn(trainingResponseModel);

        TrainingResponseModel result = trainingsService.getTrainingByTrainingId(TRAINING_ID);

        assertEquals(trainingResponseModel, result);
        verify(trainingsServiceClient, times(1)).getTrainingByTrainingId(TRAINING_ID);
    }

    @Test
    void addTraining_ShouldCallClientAndReturnCreatedTraining() {
        when(trainingsServiceClient.addTraining(trainingRequestModel)).thenReturn(trainingResponseModel);

        TrainingResponseModel result = trainingsService.addTraining(trainingRequestModel);

        assertEquals(trainingResponseModel, result);
        verify(trainingsServiceClient, times(1)).addTraining(trainingRequestModel);
    }

    @Test
    void updateTraining_ShouldCallClientAndReturnUpdatedTraining() {
        when(trainingsServiceClient.updateTraining(trainingRequestModel, TRAINING_ID)).thenReturn(trainingResponseModel);

        TrainingResponseModel result = trainingsService.updateTraining(trainingRequestModel, TRAINING_ID);

        assertEquals(trainingResponseModel, result);
        verify(trainingsServiceClient, times(1)).updateTraining(trainingRequestModel, TRAINING_ID);
    }

    @Test
    void removeTraining_ShouldCallClient() {
        doNothing().when(trainingsServiceClient).removeTraining(TRAINING_ID);

        trainingsService.removeTraining(TRAINING_ID);

        verify(trainingsServiceClient, times(1)).removeTraining(TRAINING_ID);
    }

    @Test
    void removeTraining_ShouldThrowEntityInUseException_WhenAdoptionsWithTrainingExist() {
        // Setup
        when(adoptionsServiceClient.getAdoptions(Map.of()))
                .thenReturn(Collections.singletonList(adoptionResponseModel));

        // Execute and verify
        EntityInUseException exception = assertThrows(EntityInUseException.class, () ->
                trainingsService.removeTraining(TRAINING_ID));

        assertEquals("Cannot delete training with ID: " + TRAINING_ID +
                " because it is used in existing adoptions", exception.getMessage());
        verify(adoptionsServiceClient).getAdoptions(Map.of());
        verify(trainingsServiceClient, never()).removeTraining(TRAINING_ID);
    }

    @Test
    void removeTraining_ShouldRemoveTraining_WhenNoAdoptionsWithTrainingExist() {
        // Setup - creating an adoption with a different training ID
        AdoptionResponseModel differentTraining = new AdoptionResponseModel();
        differentTraining.setTrainingId("different-training-id");

        when(adoptionsServiceClient.getAdoptions(Map.of()))
                .thenReturn(Collections.singletonList(differentTraining));

        // Execute
        trainingsService.removeTraining(TRAINING_ID);

        // Verify
        verify(adoptionsServiceClient).getAdoptions(Map.of());
        verify(trainingsServiceClient).removeTraining(TRAINING_ID);
    }
}