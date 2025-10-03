package com.creatureadoption.apigateway.businesslayer.creatures;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionStatus;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.ProfileStatus;
import com.creatureadoption.apigateway.domainclientlayer.creatures.*;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureRequestModel;
import com.creatureadoption.apigateway.presentationlayer.creatures.CreatureResponseModel;
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

class CreaturesServiceImplTest {

    @Mock
    private CreaturesServiceClient creaturesServiceClient;
    @Mock
    private AdoptionsServiceClient adoptionsServiceClient;

    @InjectMocks
    private CreaturesServiceImpl creaturesService;

    private final String CREATURE_ID = "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private CreatureResponseModel creatureResponseModel;
    private CreatureRequestModel creatureRequestModel;

    private  AdoptionResponseModel adoptionResponseModel;
    private List<CreatureResponseModel> creatureResponseModels;
    private Map<String, String> queryParams;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

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

        adoptionResponseModel = new AdoptionResponseModel(
                "a2a7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "ADO-12345",
                "Creature adoption",
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
                CREATURE_ID,  // Use the test class's creature ID
                "Sparky",
                "Dragon",
                CreatureStatus.ADOPTED,
                "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "Fire Training",
                "Academy"
        );

        creatureResponseModels = Collections.singletonList(creatureResponseModel);
        queryParams = new HashMap<>();
    }

    @Test
    void getCreatures_ShouldCallClientAndReturnCreatures() {
        when(creaturesServiceClient.getCreatures(queryParams)).thenReturn(creatureResponseModels);

        List<CreatureResponseModel> result = creaturesService.getCreatures(queryParams);

        assertEquals(creatureResponseModels, result);
        verify(creaturesServiceClient, times(1)).getCreatures(queryParams);
    }

    @Test
    void getCreatureByCreatureId_ShouldCallClientAndReturnCreature() {
        when(creaturesServiceClient.getCreatureByCreatureId(CREATURE_ID)).thenReturn(creatureResponseModel);

        CreatureResponseModel result = creaturesService.getCreatureByCreatureId(CREATURE_ID);

        assertEquals(creatureResponseModel, result);
        verify(creaturesServiceClient, times(1)).getCreatureByCreatureId(CREATURE_ID);
    }

    @Test
    void addCreature_ShouldCallClientAndReturnCreatedCreature() {
        when(creaturesServiceClient.addCreature(creatureRequestModel)).thenReturn(creatureResponseModel);

        CreatureResponseModel result = creaturesService.addCreature(creatureRequestModel);

        assertEquals(creatureResponseModel, result);
        verify(creaturesServiceClient, times(1)).addCreature(creatureRequestModel);
    }

    @Test
    void updateCreature_ShouldCallClientAndReturnUpdatedCreature() {
        when(creaturesServiceClient.updateCreature(creatureRequestModel, CREATURE_ID)).thenReturn(creatureResponseModel);

        CreatureResponseModel result = creaturesService.updateCreature(creatureRequestModel, CREATURE_ID);

        assertEquals(creatureResponseModel, result);
        verify(creaturesServiceClient, times(1)).updateCreature(creatureRequestModel, CREATURE_ID);
    }

    @Test
    void removeCreature_ShouldCallClient() {
        doNothing().when(creaturesServiceClient).removeCreature(CREATURE_ID);

        creaturesService.removeCreature(CREATURE_ID);

        verify(creaturesServiceClient, times(1)).removeCreature(CREATURE_ID);
    }

    @Test
    void removeCreature_ShouldThrowEntityInUseException_WhenAdoptionsExist() {
        // Setup
        Map<String, String> expectedQueryParams = Collections.singletonMap("creatureId", CREATURE_ID);
        when(adoptionsServiceClient.getAdoptions(expectedQueryParams))
                .thenReturn(Collections.singletonList(new AdoptionResponseModel()));

        // Execute and verify
        EntityInUseException exception = assertThrows(EntityInUseException.class, () ->
                creaturesService.removeCreature(CREATURE_ID));

        assertEquals("Cannot delete creature with ID: " + CREATURE_ID +
                " because it has existing adoptions", exception.getMessage());
        verify(adoptionsServiceClient).getAdoptions(expectedQueryParams);
        verify(creaturesServiceClient, never()).removeCreature(CREATURE_ID);
    }

    @Test
    void removeCreature_ShouldRemoveCreature_WhenNoAdoptionsExist() {
        // Setup
        Map<String, String> expectedQueryParams = Collections.singletonMap("creatureId", CREATURE_ID);
        when(adoptionsServiceClient.getAdoptions(expectedQueryParams))
                .thenReturn(Collections.emptyList());

        // Execute
        creaturesService.removeCreature(CREATURE_ID);

        // Verify
        verify(adoptionsServiceClient).getAdoptions(expectedQueryParams);
        verify(creaturesServiceClient).removeCreature(CREATURE_ID);
    }
}