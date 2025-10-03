package com.creatureadoption.adoptions.presentationlayer;

import com.creatureadoption.adoptions.businesslayer.AdoptionService;
import com.creatureadoption.adoptions.dataaccesslayer.*;
import com.creatureadoption.adoptions.domainclientlayer.CreatureServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.CustomerServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.TrainingServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.models.CreatureResponseModel;
import com.creatureadoption.adoptions.domainclientlayer.models.CustomerResponseModel;
import com.creatureadoption.adoptions.domainclientlayer.models.TrainingResponseModel;
import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AdoptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdoptionService adoptionService;

    @MockBean
    private CreatureServiceClient creatureServiceClient;

    @MockBean
    private CustomerServiceClient customerServiceClient;

    @MockBean
    private TrainingServiceClient trainingServiceClient;

    private AdoptionResponseModel adoptionResponseModel1;
    private AdoptionResponseModel adoptionResponseModel2;
    private AdoptionRequestModel adoptionRequestModel;
    private String validAdoptionId;
    private String validCustomerId;
    private String validCreatureId;
    private String validTrainingId;

    @BeforeEach
    void setUp() {
        validAdoptionId = UUID.randomUUID().toString();
        validCustomerId = UUID.randomUUID().toString();
        validCreatureId = UUID.randomUUID().toString();
        validTrainingId = UUID.randomUUID().toString();

        // Setup adoption response models
        adoptionResponseModel1 = new AdoptionResponseModel();
        adoptionResponseModel1.setAdoptionId(validAdoptionId);
        adoptionResponseModel1.setAdoptionCode("ADO-12345678");
        adoptionResponseModel1.setSummary("Test Adoption 1");
        adoptionResponseModel1.setTotalAdoptions(0);
        adoptionResponseModel1.setProfileCreationDate(LocalDate.now());
        adoptionResponseModel1.setLastUpdated(LocalDateTime.now());
        adoptionResponseModel1.setProfileStatus(ProfileStatus.ACTIVE);
        adoptionResponseModel1.setAdoptionDate(LocalDate.now().plusDays(7));
        adoptionResponseModel1.setAdoptionLocation("Test Location 1");
        adoptionResponseModel1.setAdoptionStatus(AdoptionStatus.PENDING);
        adoptionResponseModel1.setSpecialNotes("Test Notes 1");
        adoptionResponseModel1.setCustomerId(validCustomerId);
        adoptionResponseModel1.setCustomerFirstName("John");
        adoptionResponseModel1.setCustomerLastName("Doe");
        adoptionResponseModel1.setCreatureId(validCreatureId);
        adoptionResponseModel1.setCreatureName("Pikachu");
        adoptionResponseModel1.setCreatureSpecies("Electric Mouse");
        adoptionResponseModel1.setCreatureStatus(CreatureStatus.ADOPTION_PENDING);
        adoptionResponseModel1.setTrainingId(validTrainingId);
        adoptionResponseModel1.setTrainingName("Basic Training");
        adoptionResponseModel1.setTrainingLocation("Training Center 1");

        adoptionResponseModel2 = new AdoptionResponseModel();
        adoptionResponseModel2.setAdoptionId(UUID.randomUUID().toString());
        adoptionResponseModel2.setAdoptionCode("ADO-87654321");
        adoptionResponseModel2.setSummary("Test Adoption 2");
        adoptionResponseModel2.setTotalAdoptions(1);
        adoptionResponseModel2.setProfileCreationDate(LocalDate.now().minusDays(5));
        adoptionResponseModel2.setLastUpdated(LocalDateTime.now());
        adoptionResponseModel2.setProfileStatus(ProfileStatus.ACTIVE);
        adoptionResponseModel2.setAdoptionDate(LocalDate.now().plusDays(14));
        adoptionResponseModel2.setAdoptionLocation("Test Location 2");
        adoptionResponseModel2.setAdoptionStatus(AdoptionStatus.APPROVED);
        adoptionResponseModel2.setSpecialNotes("Test Notes 2");
        adoptionResponseModel2.setCustomerId(UUID.randomUUID().toString());
        adoptionResponseModel2.setCustomerFirstName("Jane");
        adoptionResponseModel2.setCustomerLastName("Smith");
        adoptionResponseModel2.setCreatureId(UUID.randomUUID().toString());
        adoptionResponseModel2.setCreatureName("Bulbasaur");
        adoptionResponseModel2.setCreatureSpecies("Seed Pokemon");
        adoptionResponseModel2.setCreatureStatus(CreatureStatus.RESERVED);
        adoptionResponseModel2.setTrainingId(UUID.randomUUID().toString());
        adoptionResponseModel2.setTrainingName("Advanced Training");
        adoptionResponseModel2.setTrainingLocation("Training Center 2");

        // Setup adoption request model
        adoptionRequestModel = AdoptionRequestModel.builder()
                .summary("New Adoption")
                .totalAdoptions(0)
                .profileCreationDate(LocalDate.now())
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(10))
                .adoptionLocation("New Location")
                .adoptionStatus(AdoptionStatus.PENDING)
                .specialNotes("New Notes")
                .customerId(validCustomerId)
                .creatureId(validCreatureId)
                .trainingId(validTrainingId)
                .build();

        // Mock service responses
        when(customerServiceClient.getCustomerByCustomerId(validCustomerId)).thenReturn(
                CustomerResponseModel.builder()
                        .customerId(validCustomerId)
                        .firstName("John")
                        .lastName("Doe")
                        .build()
        );

        when(creatureServiceClient.getCreatureByCreatureId(validCreatureId)).thenReturn(
                CreatureResponseModel.builder()
                        .creatureId(validCreatureId)
                        .name("Pikachu")
                        .species("Electric Mouse")
                        .status(CreatureStatus.AVAILABLE.toString())
                        .build()
        );

        when(trainingServiceClient.getTrainingByTrainingId(validTrainingId)).thenReturn(
                TrainingResponseModel.builder()
                        .trainingId(validTrainingId)
                        .name("Basic Training")
                        .location("Training Center 1")
                        .build()
        );
    }

    @Test
    void getAdoptions_ShouldReturnAllAdoptions() throws Exception {
        // Arrange
        List<AdoptionResponseModel> adoptions = Arrays.asList(adoptionResponseModel1, adoptionResponseModel2);
        when(adoptionService.getAdoptions(any())).thenReturn(adoptions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].adoptionId", is(validAdoptionId)))
                .andExpect(jsonPath("$[0].summary", is("Test Adoption 1")))
                .andExpect(jsonPath("$[1].summary", is("Test Adoption 2")));
    }

    @Test
    void getAdoptionByAdoptionId_WithValidId_ShouldReturnAdoption() throws Exception {
        // Arrange
        when(adoptionService.getAdoptionByAdoptionId(validAdoptionId)).thenReturn(adoptionResponseModel1);

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions/{adoptionId}", validAdoptionId))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.adoptionId").value(validAdoptionId))
                .andExpect(jsonPath("$.summary").value("Test Adoption 1"))
                .andExpect(jsonPath("$.adoptionStatus").value("PENDING"));

        // Verify service was called
        verify(adoptionService).getAdoptionByAdoptionId(validAdoptionId);
    }

    @Test
    void getAdoptionByAdoptionId_WithInvalidId_ShouldReturn404() throws Exception {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions/{adoptionId}", invalidId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAdoptionByAdoptionId_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(adoptionService.getAdoptionByAdoptionId(nonExistentId))
                .thenThrow(new NotFoundException("Adoption not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions/{adoptionId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addAdoption_WithValidData_ShouldReturnCreatedAdoption() throws Exception {
        // Arrange
        when(adoptionService.addAdoption(any(AdoptionRequestModel.class))).thenReturn(adoptionResponseModel1);

        // Act & Assert
        mockMvc.perform(post("/api/v1/adoptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isCreated())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.adoptionId").exists())
                .andExpect(jsonPath("$.summary").value("Test Adoption 1"));

        // Verify service was called with correct parameters
        verify(adoptionService).addAdoption(any(AdoptionRequestModel.class));
    }

    @Test
    void addAdoption_WithCustomerNotFound_ShouldReturn404() throws Exception {
        // Arrange
        when(adoptionService.addAdoption(any(AdoptionRequestModel.class)))
                .thenThrow(new NotFoundException("Customer not found"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/adoptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addAdoption_WithCreatureNotFound_ShouldReturn404() throws Exception {
        // Arrange
        when(adoptionService.addAdoption(any(AdoptionRequestModel.class)))
                .thenThrow(new NotFoundException("Creature not found"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/adoptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isNotFound());
    }

    @Test
    void addAdoption_WithInvalidCreatureStatus_ShouldReturn422() throws Exception {
        // Arrange
        when(adoptionService.addAdoption(any(AdoptionRequestModel.class)))
                .thenThrow(new InvalidInputException("Creature is not available for adoption"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/adoptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void addAdoption_WithAdoptionLimitExceeded_ShouldReturn403() throws Exception {
        // Arrange
        when(adoptionService.addAdoption(any(AdoptionRequestModel.class)))
                .thenThrow(new AdoptionLimitExceededException("Customer has reached the maximum limit of adoptions"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/adoptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAdoption_WithValidData_ShouldReturnUpdatedAdoption() throws Exception {
        // Arrange
        when(adoptionService.updateAdoption(any(AdoptionRequestModel.class), eq(validAdoptionId)))
                .thenReturn(adoptionResponseModel1);

        // Act & Assert
        mockMvc.perform(put("/api/v1/adoptions/{adoptionId}", validAdoptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.adoptionId").exists());

        // Verify service was called with correct parameters
        verify(adoptionService).updateAdoption(any(AdoptionRequestModel.class), eq(validAdoptionId));
    }

    @Test
    void updateAdoption_WithInvalidId_ShouldReturn422() throws Exception {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        mockMvc.perform(put("/api/v1/adoptions/{adoptionId}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateAdoption_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(adoptionService.updateAdoption(any(AdoptionRequestModel.class), eq(nonExistentId)))
                .thenThrow(new NotFoundException("Adoption not found"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/adoptions/{adoptionId}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adoptionRequestModel)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateAdoptionStatus_WithValidData_ShouldReturnUpdatedAdoption() throws Exception {
        // Arrange
        when(adoptionService.updateAdoptionStatus(eq(validAdoptionId), eq("COMPLETED")))
                .thenReturn(adoptionResponseModel1);

        // Act & Assert
        mockMvc.perform(patch("/api/v1/adoptions/{adoptionId}/status/{status}", validAdoptionId, "COMPLETED"))
                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.adoptionId").exists());

        // Verify service was called with correct parameters
        verify(adoptionService).updateAdoptionStatus(eq(validAdoptionId), eq("COMPLETED"));
    }

    @Test
    void updateAdoptionStatus_WithInvalidId_ShouldReturn422() throws Exception {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        mockMvc.perform(patch("/api/v1/adoptions/{adoptionId}/status/{status}", invalidId, "COMPLETED"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateAdoptionStatus_WithInvalidStatus_ShouldReturn422() throws Exception {
        // Arrange
        when(adoptionService.updateAdoptionStatus(validAdoptionId, "INVALID_STATUS"))
                .thenThrow(new InvalidInputException("Invalid adoption status"));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/adoptions/{adoptionId}/status/{status}", validAdoptionId, "INVALID_STATUS"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateAdoptionStatus_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        when(adoptionService.updateAdoptionStatus(nonExistentId, "COMPLETED"))
                .thenThrow(new NotFoundException("Adoption not found"));

        // Act & Assert
        mockMvc.perform(patch("/api/v1/adoptions/{adoptionId}/status/{status}", nonExistentId, "COMPLETED"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAdoption_WithValidId_ShouldReturn204() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/adoptions/{adoptionId}", validAdoptionId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAdoption_WithInvalidId_ShouldReturn422() throws Exception {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        mockMvc.perform(delete("/api/v1/adoptions/{adoptionId}", invalidId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void deleteAdoption_WithNonExistentId_ShouldReturn404() throws Exception {
        // Arrange
        String nonExistentId = UUID.randomUUID().toString();
        doThrow(new NotFoundException("Adoption not found"))
                .when(adoptionService).removeAdoption(nonExistentId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/adoptions/{adoptionId}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAdoption_WithCompletedAdoption_ShouldReturn422() throws Exception {
        // Arrange
        doThrow(new InvalidInputException("Cannot remove a completed adoption"))
                .when(adoptionService).removeAdoption(validAdoptionId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/adoptions/{adoptionId}", validAdoptionId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAdoptions_WithCustomerId_ShouldFilterByCustomerId() throws Exception {
        // Arrange
        List<AdoptionResponseModel> filteredAdoptions = Collections.singletonList(adoptionResponseModel1);
        when(adoptionService.getAdoptions(Collections.singletonMap("customerId", validCustomerId)))
                .thenReturn(filteredAdoptions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("customerId", validCustomerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId", is(validCustomerId)));
    }

    @Test
    void getAdoptions_WithInvalidCustomerId_ShouldReturn422() throws Exception {
        // Arrange
        String invalidCustomerId = "invalid-id"; // Not UUID format

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("customerId", invalidCustomerId))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getAdoptions_WithCreatureId_ShouldFilterByCreatureId() throws Exception {
        // Arrange
        List<AdoptionResponseModel> filteredAdoptions = Collections.singletonList(adoptionResponseModel1);
        when(adoptionService.getAdoptions(Collections.singletonMap("creatureId", validCreatureId)))
                .thenReturn(filteredAdoptions);

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("creatureId", validCreatureId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].creatureId", is(validCreatureId)));
    }

    @Test
    void getAdoptions_WithInvalidCreatureId_ShouldReturn422() throws Exception {
        // Arrange
        String invalidCreatureId = "invalid-id"; // Not UUID format

        // Act & Assert
        mockMvc.perform(get("/api/v1/adoptions")
                        .param("creatureId", invalidCreatureId))
                .andExpect(status().isUnprocessableEntity());
    }

    private <T> T eq(T value) {
        return org.mockito.ArgumentMatchers.eq(value);
    }
}