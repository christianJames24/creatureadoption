package com.creatureadoption.adoptions.presentationlayer;

import com.creatureadoption.adoptions.businesslayer.AdoptionService;
import com.creatureadoption.adoptions.dataaccesslayer.AdoptionStatus;
import com.creatureadoption.adoptions.dataaccesslayer.CreatureStatus;
import com.creatureadoption.adoptions.dataaccesslayer.ProfileStatus;
import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdoptionControllerUnitTest {

    @Mock
    private AdoptionService adoptionService;

    @InjectMocks
    private AdoptionController adoptionController;

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
    }

    @Test
    void getAdoptions_ShouldReturnAllAdoptions() {
        // Arrange
        Map<String, String> queryParams = new HashMap<>();
        List<AdoptionResponseModel> expectedAdoptions = Arrays.asList(adoptionResponseModel1, adoptionResponseModel2);
        when(adoptionService.getAdoptions(queryParams)).thenReturn(expectedAdoptions);

        // Act
        ResponseEntity<List<AdoptionResponseModel>> response = adoptionController.getAdoptions(queryParams);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAdoptions, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(adoptionService).getAdoptions(queryParams);
    }

    @Test
    void getAdoptions_WithCustomerId_ShouldFilterByCustomerId() {
        // Arrange
        Map<String, String> queryParams = Collections.singletonMap("customerId", validCustomerId);
        List<AdoptionResponseModel> expectedAdoptions = Collections.singletonList(adoptionResponseModel1);
        when(adoptionService.getAdoptions(queryParams)).thenReturn(expectedAdoptions);

        // Act
        ResponseEntity<List<AdoptionResponseModel>> response = adoptionController.getAdoptions(queryParams);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAdoptions, response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(validCustomerId, response.getBody().get(0).getCustomerId());
        verify(adoptionService).getAdoptions(queryParams);
    }

    @Test
    void getAdoptions_WithInvalidCustomerId_ShouldThrowException() {
        // Arrange
        Map<String, String> queryParams = Collections.singletonMap("customerId", "invalid-id");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> adoptionController.getAdoptions(queryParams));
        verify(adoptionService, never()).getAdoptions(anyMap());
    }

    @Test
    void getAdoptions_WithCreatureId_ShouldFilterByCreatureId() {
        // Arrange
        Map<String, String> queryParams = Collections.singletonMap("creatureId", validCreatureId);
        List<AdoptionResponseModel> expectedAdoptions = Collections.singletonList(adoptionResponseModel1);
        when(adoptionService.getAdoptions(queryParams)).thenReturn(expectedAdoptions);

        // Act
        ResponseEntity<List<AdoptionResponseModel>> response = adoptionController.getAdoptions(queryParams);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAdoptions, response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(validCreatureId, response.getBody().get(0).getCreatureId());
        verify(adoptionService).getAdoptions(queryParams);
    }

    @Test
    void getAdoptions_WithInvalidCreatureId_ShouldThrowException() {
        // Arrange
        Map<String, String> queryParams = Collections.singletonMap("creatureId", "invalid-id");

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> adoptionController.getAdoptions(queryParams));
        verify(adoptionService, never()).getAdoptions(anyMap());
    }

    @Test
    void getAdoptionByAdoptionId_WithValidId_ShouldReturnAdoption() {
        // Arrange
        when(adoptionService.getAdoptionByAdoptionId(validAdoptionId)).thenReturn(adoptionResponseModel1);

        // Act
        ResponseEntity<AdoptionResponseModel> response = adoptionController.getAdoptionByAdoptionId(validAdoptionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adoptionResponseModel1, response.getBody());
        assertEquals(validAdoptionId, response.getBody().getAdoptionId());
        verify(adoptionService).getAdoptionByAdoptionId(validAdoptionId);
    }

    @Test
    void getAdoptionByAdoptionId_WithInvalidId_ShouldThrowException() {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> adoptionController.getAdoptionByAdoptionId(invalidId));
        verify(adoptionService, never()).getAdoptionByAdoptionId(anyString());
    }

    @Test
    void getAdoptionByAdoptionId_WithServiceReturningNotFound_ShouldPropagateException() {
        // Arrange
        when(adoptionService.getAdoptionByAdoptionId(validAdoptionId))
                .thenThrow(new NotFoundException("Adoption not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> adoptionController.getAdoptionByAdoptionId(validAdoptionId));
        verify(adoptionService).getAdoptionByAdoptionId(validAdoptionId);
    }

    @Test
    void addAdoption_WithValidData_ShouldReturnCreatedAdoption() {
        // Arrange
        when(adoptionService.addAdoption(adoptionRequestModel)).thenReturn(adoptionResponseModel1);

        // Act
        ResponseEntity<AdoptionResponseModel> response = adoptionController.addAdoption(adoptionRequestModel);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(adoptionResponseModel1, response.getBody());
        verify(adoptionService).addAdoption(adoptionRequestModel);
    }

    @Test
    void addAdoption_WithServiceReturningNotFoundException_ShouldPropagateException() {
        // Arrange
        when(adoptionService.addAdoption(adoptionRequestModel))
                .thenThrow(new NotFoundException("Customer not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> adoptionController.addAdoption(adoptionRequestModel));
        verify(adoptionService).addAdoption(adoptionRequestModel);
    }

    @Test
    void addAdoption_WithServiceReturningInvalidInputException_ShouldPropagateException() {
        // Arrange
        when(adoptionService.addAdoption(adoptionRequestModel))
                .thenThrow(new InvalidInputException("Invalid input"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> adoptionController.addAdoption(adoptionRequestModel));
        verify(adoptionService).addAdoption(adoptionRequestModel);
    }

    @Test
    void addAdoption_WithServiceReturningAdoptionLimitExceededException_ShouldPropagateException() {
        // Arrange
        when(adoptionService.addAdoption(adoptionRequestModel))
                .thenThrow(new AdoptionLimitExceededException("Adoption limit exceeded"));

        // Act & Assert
        assertThrows(AdoptionLimitExceededException.class, () -> adoptionController.addAdoption(adoptionRequestModel));
        verify(adoptionService).addAdoption(adoptionRequestModel);
    }

    @Test
    void updateAdoption_WithValidData_ShouldReturnUpdatedAdoption() {
        // Arrange
        when(adoptionService.updateAdoption(adoptionRequestModel, validAdoptionId))
                .thenReturn(adoptionResponseModel1);

        // Act
        ResponseEntity<AdoptionResponseModel> response = adoptionController.updateAdoption(
                adoptionRequestModel, validAdoptionId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adoptionResponseModel1, response.getBody());
        verify(adoptionService).updateAdoption(adoptionRequestModel, validAdoptionId);
    }

    @Test
    void updateAdoption_WithInvalidId_ShouldThrowException() {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                adoptionController.updateAdoption(adoptionRequestModel, invalidId));
        verify(adoptionService, never()).updateAdoption(any(), anyString());
    }

    @Test
    void updateAdoption_WithServiceReturningNotFoundException_ShouldPropagateException() {
        // Arrange
        when(adoptionService.updateAdoption(adoptionRequestModel, validAdoptionId))
                .thenThrow(new NotFoundException("Adoption not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                adoptionController.updateAdoption(adoptionRequestModel, validAdoptionId));
        verify(adoptionService).updateAdoption(adoptionRequestModel, validAdoptionId);
    }

    @Test
    void updateAdoptionStatus_WithValidData_ShouldReturnUpdatedAdoption() {
        // Arrange
        String newStatus = "COMPLETED";
        when(adoptionService.updateAdoptionStatus(validAdoptionId, newStatus))
                .thenReturn(adoptionResponseModel1);

        // Act
        ResponseEntity<AdoptionResponseModel> response = adoptionController.updateAdoptionStatus(
                validAdoptionId, newStatus);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(adoptionResponseModel1, response.getBody());
        verify(adoptionService).updateAdoptionStatus(validAdoptionId, newStatus);
    }

    @Test
    void updateAdoptionStatus_WithInvalidId_ShouldThrowException() {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format
        String newStatus = "COMPLETED";

        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                adoptionController.updateAdoptionStatus(invalidId, newStatus));
        verify(adoptionService, never()).updateAdoptionStatus(anyString(), anyString());
    }

    @Test
    void updateAdoptionStatus_WithServiceReturningNotFoundException_ShouldPropagateException() {
        // Arrange
        String newStatus = "COMPLETED";
        when(adoptionService.updateAdoptionStatus(validAdoptionId, newStatus))
                .thenThrow(new NotFoundException("Adoption not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () ->
                adoptionController.updateAdoptionStatus(validAdoptionId, newStatus));
        verify(adoptionService).updateAdoptionStatus(validAdoptionId, newStatus);
    }

    @Test
    void updateAdoptionStatus_WithServiceReturningInvalidInputException_ShouldPropagateException() {
        // Arrange
        String invalidStatus = "INVALID_STATUS";
        when(adoptionService.updateAdoptionStatus(validAdoptionId, invalidStatus))
                .thenThrow(new InvalidInputException("Invalid status"));

        // Act & Assert
        assertThrows(InvalidInputException.class, () ->
                adoptionController.updateAdoptionStatus(validAdoptionId, invalidStatus));
        verify(adoptionService).updateAdoptionStatus(validAdoptionId, invalidStatus);
    }

    @Test
    void deleteAdoption_WithValidId_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = adoptionController.deleteAdoption(validAdoptionId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(adoptionService).removeAdoption(validAdoptionId);
    }

    @Test
    void deleteAdoption_WithInvalidId_ShouldThrowException() {
        // Arrange
        String invalidId = "invalid-id"; // Not UUID format

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> adoptionController.deleteAdoption(invalidId));
        verify(adoptionService, never()).removeAdoption(anyString());
    }

    @Test
    void deleteAdoption_WithServiceReturningNotFoundException_ShouldPropagateException() {
        // Arrange
        doThrow(new NotFoundException("Adoption not found"))
                .when(adoptionService).removeAdoption(validAdoptionId);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> adoptionController.deleteAdoption(validAdoptionId));
        verify(adoptionService).removeAdoption(validAdoptionId);
    }

    @Test
    void deleteAdoption_WithServiceReturningInvalidInputException_ShouldPropagateException() {
        // Arrange
        doThrow(new InvalidInputException("Cannot delete completed adoption"))
                .when(adoptionService).removeAdoption(validAdoptionId);

        // Act & Assert
        assertThrows(InvalidInputException.class, () -> adoptionController.deleteAdoption(validAdoptionId));
        verify(adoptionService).removeAdoption(validAdoptionId);
    }
}