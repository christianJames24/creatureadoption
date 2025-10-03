package com.creatureadoption.adoptions.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class AdoptionRepositoryIntegrationTest {

    @Autowired
    private AdoptionRepository adoptionRepository;

    private Adoption adoption1;
    private Adoption adoption2;
    private String customerId1;
    private String customerId2;
    private String creatureId1;
    private String creatureId2;

    @BeforeEach
    void setUp() {
        // Clear the repository
        adoptionRepository.deleteAll();

        // Create test data
        customerId1 = UUID.randomUUID().toString();
        customerId2 = UUID.randomUUID().toString();
        creatureId1 = UUID.randomUUID().toString();
        creatureId2 = UUID.randomUUID().toString();
        String trainingId1 = UUID.randomUUID().toString();
        String trainingId2 = UUID.randomUUID().toString();

        // Create first adoption
        adoption1 = Adoption.builder()
                .adoptionIdentifier(new AdoptionIdentifier("TEST-CODE-1"))
                .summary("Test Adoption 1")
                .totalAdoptions(0)
                .profileCreationDate(LocalDate.now())
                .lastUpdated(LocalDateTime.now())
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(7))
                .adoptionLocation("Test Location 1")
                .adoptionStatus(AdoptionStatus.PENDING)
                .specialNotes("Test Notes 1")
                .customerId(customerId1)
                .creatureId(creatureId1)
                .trainingId(trainingId1)
                .build();

        // Create second adoption
        adoption2 = Adoption.builder()
                .adoptionIdentifier(new AdoptionIdentifier("TEST-CODE-2"))
                .summary("Test Adoption 2")
                .totalAdoptions(1)
                .profileCreationDate(LocalDate.now().minusDays(10))
                .lastUpdated(LocalDateTime.now())
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(14))
                .adoptionLocation("Test Location 2")
                .adoptionStatus(AdoptionStatus.APPROVED)
                .specialNotes("Test Notes 2")
                .customerId(customerId2)
                .creatureId(creatureId2)
                .trainingId(trainingId2)
                .build();

        // Save test data
        adoption1 = adoptionRepository.save(adoption1);
        adoption2 = adoptionRepository.save(adoption2);
    }

    @Test
    void findAll_ShouldReturnAllAdoptions() {
        // Act
        List<Adoption> foundAdoptions = adoptionRepository.findAll();

        // Assert
        assertEquals(2, foundAdoptions.size());
        assertTrue(foundAdoptions.stream()
                .anyMatch(a -> a.getAdoptionIdentifier().getAdoptionCode().equals("TEST-CODE-1")));
        assertTrue(foundAdoptions.stream()
                .anyMatch(a -> a.getAdoptionIdentifier().getAdoptionCode().equals("TEST-CODE-2")));
    }

    @Test
    void findByAdoptionIdentifier_AdoptionId_ShouldReturnCorrectAdoption() {
        // Arrange
        String adoptionId = adoption1.getAdoptionIdentifier().getAdoptionId();

        // Act
        Adoption foundAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId);

        // Assert
        assertNotNull(foundAdoption);
        assertEquals("Test Adoption 1", foundAdoption.getSummary());
        assertEquals(adoptionId, foundAdoption.getAdoptionIdentifier().getAdoptionId());
    }

    @Test
    void findByAdoptionIdentifier_AdoptionId_WithInvalidId_ShouldReturnNull() {
        // Act
        Adoption foundAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId("non-existent-id");

        // Assert
        assertNull(foundAdoption);
    }

    @Test
    void findByCustomerId_ShouldReturnCorrectAdoptions() {
        // Act
        List<Adoption> foundAdoptions = adoptionRepository.findByCustomerId(customerId1);

        // Assert
        assertEquals(1, foundAdoptions.size());
        assertEquals(customerId1, foundAdoptions.get(0).getCustomerId());
        assertEquals("Test Adoption 1", foundAdoptions.get(0).getSummary());
    }

    @Test
    void findByCustomerId_WithNonExistentId_ShouldReturnEmptyList() {
        // Act
        List<Adoption> foundAdoptions = adoptionRepository.findByCustomerId("non-existent-id");

        // Assert
        assertTrue(foundAdoptions.isEmpty());
    }

    @Test
    void findByCreatureId_ShouldReturnCorrectAdoptions() {
        // Act
        List<Adoption> foundAdoptions = adoptionRepository.findByCreatureId(creatureId2);

        // Assert
        assertEquals(1, foundAdoptions.size());
        assertEquals(creatureId2, foundAdoptions.get(0).getCreatureId());
        assertEquals("Test Adoption 2", foundAdoptions.get(0).getSummary());
    }

    @Test
    void findByCreatureId_WithNonExistentId_ShouldReturnEmptyList() {
        // Act
        List<Adoption> foundAdoptions = adoptionRepository.findByCreatureId("non-existent-id");

        // Assert
        assertTrue(foundAdoptions.isEmpty());
    }

    @Test
    void findByAdoptionStatus_ShouldReturnCorrectAdoptions() {
        // Act
        List<Adoption> pendingAdoptions = adoptionRepository.findByAdoptionStatus(AdoptionStatus.PENDING);
        List<Adoption> approvedAdoptions = adoptionRepository.findByAdoptionStatus(AdoptionStatus.APPROVED);
        List<Adoption> completedAdoptions = adoptionRepository.findByAdoptionStatus(AdoptionStatus.COMPLETED);

        // Assert
        assertEquals(1, pendingAdoptions.size());
        assertEquals(1, approvedAdoptions.size());
        assertTrue(completedAdoptions.isEmpty());

        assertEquals("Test Adoption 1", pendingAdoptions.get(0).getSummary());
        assertEquals("Test Adoption 2", approvedAdoptions.get(0).getSummary());
    }

    @Test
    void findByProfileStatus_ShouldReturnCorrectAdoptions() {
        // Act
        List<Adoption> activeAdoptions = adoptionRepository.findByProfileStatus(ProfileStatus.ACTIVE);
        List<Adoption> inactiveAdoptions = adoptionRepository.findByProfileStatus(ProfileStatus.INACTIVE);

        // Assert
        assertEquals(2, activeAdoptions.size());
        assertTrue(inactiveAdoptions.isEmpty());
    }

    @Test
    void saveAdoption_ShouldStoreCorrectData() {
        // Arrange
        Adoption newAdoption = Adoption.builder()
                .adoptionIdentifier(new AdoptionIdentifier("TEST-CODE-3"))
                .summary("Test Adoption 3")
                .totalAdoptions(2)
                .profileCreationDate(LocalDate.now().minusDays(5))
                .lastUpdated(LocalDateTime.now())
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(21))
                .adoptionLocation("Test Location 3")
                .adoptionStatus(AdoptionStatus.COMPLETED)
                .specialNotes("Test Notes 3")
                .customerId(UUID.randomUUID().toString())
                .creatureId(UUID.randomUUID().toString())
                .trainingId(UUID.randomUUID().toString())
                .build();

        // Act
        Adoption savedAdoption = adoptionRepository.save(newAdoption);

        // Assert
        assertNotNull(savedAdoption.getId());
        assertEquals("Test Adoption 3", savedAdoption.getSummary());
        assertEquals(AdoptionStatus.COMPLETED, savedAdoption.getAdoptionStatus());

        // Verify it's in the repository
        Adoption retrievedAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(
                savedAdoption.getAdoptionIdentifier().getAdoptionId());
        assertNotNull(retrievedAdoption);
        assertEquals("TEST-CODE-3", retrievedAdoption.getAdoptionIdentifier().getAdoptionCode());
    }

    @Test
    void deleteAdoption_ShouldRemoveFromRepository() {
        // Arrange
        long initialCount = adoptionRepository.count();

        // Act
        adoptionRepository.delete(adoption1);

        // Assert
        assertEquals(initialCount - 1, adoptionRepository.count());
        assertNull(adoptionRepository.findByAdoptionIdentifier_AdoptionId(
                adoption1.getAdoptionIdentifier().getAdoptionId()));
    }

    @Test
    void updateAdoption_ShouldUpdateCorrectFields() {
        // Arrange
        adoption1.setSummary("Updated Summary");
        adoption1.setAdoptionStatus(AdoptionStatus.COMPLETED);
        adoption1.setSpecialNotes("Updated Notes");

        // Act
        Adoption updatedAdoption = adoptionRepository.save(adoption1);

        // Assert
        assertEquals("Updated Summary", updatedAdoption.getSummary());
        assertEquals(AdoptionStatus.COMPLETED, updatedAdoption.getAdoptionStatus());
        assertEquals("Updated Notes", updatedAdoption.getSpecialNotes());

        // Verify in repository
        Adoption retrievedAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(
                adoption1.getAdoptionIdentifier().getAdoptionId());
        assertEquals("Updated Summary", retrievedAdoption.getSummary());
        assertEquals(AdoptionStatus.COMPLETED, retrievedAdoption.getAdoptionStatus());
    }
}