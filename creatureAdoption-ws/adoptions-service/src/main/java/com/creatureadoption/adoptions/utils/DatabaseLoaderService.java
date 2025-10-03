package com.creatureadoption.adoptions.utils;

import com.creatureadoption.adoptions.dataaccesslayer.*;
import com.creatureadoption.adoptions.domainclientlayer.CreatureServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.CustomerServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.TrainingServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.models.CreatureResponseModel;
import com.creatureadoption.adoptions.domainclientlayer.models.CustomerResponseModel;
import com.creatureadoption.adoptions.domainclientlayer.models.TrainingResponseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile({"default", "test", "docker"})
@RequiredArgsConstructor
public class DatabaseLoaderService implements CommandLineRunner {

    private final AdoptionRepository adoptionRepository;
    private final CustomerServiceClient customerServiceClient;
    private final CreatureServiceClient creatureServiceClient;
    private final TrainingServiceClient trainingServiceClient;

    @Override
    public void run(String... args) throws Exception {
        log.info("Loading sample adoption data into MongoDB...");

        // Check if data already exists
//        if (adoptionRepository.count() > 0) {
//            log.info("Database already contains adoption data. Skipping initialization.");
//            return;
//        }
        log.info("Clearing existing adoption data...");
        adoptionRepository.deleteAll();

        // Sample data
        createAdoption(
                "76db80b7-5f9d-4549-8a94-83d44c43bce6",
                "8a4b5c6d-7e8f-9a0b-1c2d-3e4f5a6b7c8d", // creatureId
                "6f8d2e53-9b4c-48a7-91fe-c508dde7817a", // customerId
                "a9b8c7d6-e5f4-g3h2-i1j0-k9l8m7n6o5p4", // trainingId
                "Electric type adoption profile",
                1,
                LocalDate.now().minusDays(15),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(10),
                "Nimbasa City Adoption Center",
                AdoptionStatus.APPROVED,
                "Special notes for electric type"
        );

        createAdoption(
                "2b2fad07-9c49-4feb-b08f-a59e4b49a207",
                "9b0c1d2e-3f4a-5b6c-7d8e-9f0a1b2c3d4e", // creatureId
                "a3b7c9d1-e5f0-4a2b-8c9d-0e1f2a3b4c5d", // customerId
                "b8c7d6e5-f4g3-h2i1-j0k9-l8m7n6o5p4q3", // trainingId
                "Fire type adoption profile",
                0,
                LocalDate.now().minusDays(5),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(15),
                "Castelia City Adoption Center",
                AdoptionStatus.PENDING,
                "Special notes for fire type"
        );

        createAdoption(
                "2c4b3338-4e52-4ac0-bdd1-e9979a7b1542",
                "0c1d2e3f-4a5b-6c7d-8e9f-0a1b2c3d4e5f", // creatureId
                "7d8e9f0a-1b2c-3d4e-5f6a-7b8c9d0e1f2a", // customerId
                "c7d6e5f4-g3h2-i1j0-k9l8-m7n6o5p4q3r2", // trainingId
                "Water type adoption profile",
                0,
                LocalDate.now().minusDays(10),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(20),
                "Pastoria City Adoption Center",
                AdoptionStatus.COMPLETED,
                "Special notes for water type"
        );


        // NEW TEST DATA
        // WAIT-FOR-SERVICE - throw-away adoption for service readiness check
        createAdoption(
                "03441080-2893-48c6-892b-1d4f63f694ef",
                "ce2706bd-cb1b-45bf-bb1f-1012643fd921",
                "65206907-3648-47d7-80d1-96e6f364b168",
                "f6794db9-81f3-4e3d-91a7-004bacd45403",
                "Temporary throw-away adoption for waitForService test",
                0,
                LocalDate.now().minusDays(1),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(1),
                "Test Adoption Center",
                AdoptionStatus.PENDING,
                "Non-consequential adoption for waitForService delete"
        );

        // POST - Success path
        // (Clay adopting Aurora with Ice Mastery training)
        createAdoption(
                "797e8df2-5ddf-465e-a9ca-8407d3f42282",
                "d1e2f3a4-b5c6-d7e8-f9a0-b1c2d3e4f5a6", // creatureId (Aurora)
                "d4e5f6a7-b8c9-d0e1-f2a3-b4c5d6e7f8a9", // customerId (Clay)
                "k9l8m7n6-o5p4-q3r2-s1t0-u9v8w7x6y5z4", // trainingId (Ice Mastery)
                "Ice type adoption profile - POST success test",
                0,
                LocalDate.now().minusDays(3),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(25),
                "Icirrus City Adoption Center",
                AdoptionStatus.PENDING,
                "Testing POST success path"
        );

        // POST - Exception path
        // (Lenora with 2 existing completed adoptions)
        // First completed adoption for Lenora
        createAdoption(
                "3faf0394-838c-49f4-807b-f9dd1677fda2",
                "6c7d8e9f-0a1b-2c3d-4e5f-6a7b8c9d0e1f", // creatureId (Wave - already adopted)
                "e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8a9b0", // customerId (Lenora)
                "f4g3h2i1-j0k9-l8m7-n6o5-p4q3r2s1t0u9", // trainingId (Water Techniques)
                "First adoption for Lenora - completed",
                1,
                LocalDate.now().minusDays(60),
                ProfileStatus.INACTIVE,
                LocalDate.now().minusDays(30),
                "Nacrene Museum Adoption Center",
                AdoptionStatus.COMPLETED,
                "First completed adoption for Lenora"
        );

        // Second completed adoption for Lenora
        createAdoption(
                "7889f109-0d6c-48a1-8a00-a3de60d8456f",
                "8d9e0f1a-2b3c-4d5e-6f7a-8b9c0d1e2f3a", // creatureId (Blaze - already adopted)
                "e5f6a7b8-c9d0-e1f2-a3b4-c5d6e7f8a9b0", // customerId (Lenora)
                "b8c7d6e5-f4g3-h2i1-j0k9-l8m7n6o5p4q3", // trainingId (Elemental Control)
                "Second adoption for Lenora - completed",
                2,
                LocalDate.now().minusDays(45),
                ProfileStatus.INACTIVE,
                LocalDate.now().minusDays(15),
                "Nacrene Museum Adoption Center",
                AdoptionStatus.COMPLETED,
                "Second completed adoption for Lenora"
        );

        // Third adoption attempt will cause exception
        // Note: This won't actually be created but is here for reference
        // When testing: try to adopt "e2f3a4b5-c6d7-e8f9-a0b1-c2d3e4f5a6b7" (Quake) to Lenora

        // PUT - Success path
        createAdoption(
                "4cf74c98-7047-4b77-832a-7745aa17a2bc",
                "f3a4b5c6-d7e8-f9a0-b1c2-d3e4f5a6b7c8", // creatureId (Wisp)
                "f6a7b8c9-d0e1-f2a3-b4c5-d6e7f8a9b0c1", // customerId (Gardenia)
                "m7n6o5p4-q3r2-s1t0-u9v8-w7x6y5z4a3b2", // trainingId (Ghost Communication)
                "Ghost type adoption profile - PUT success test",
                0,
                LocalDate.now().minusDays(7),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(14),
                "Old Chateau Adoption Center",
                AdoptionStatus.PENDING,
                "Testing PUT success path - can be updated"
        );

        // PUT - Exception path
        createAdoption(
                "ca0b8dc9-f104-43d1-8c80-501118043a60",
                "a4b5c6d7-e8f9-a0b1-c2d3-e4f5a6b7c8d9", // creatureId (Glow)
                "f6a7b8c9-d0e1-f2a3-b4c5-d6e7f8a9b0c1", // customerId (Gardenia)
                "n6o5p4q3-r2s1-t0u9-v8w7-x6y5z4a3b2c1", // trainingId (Water Acrobatics)
                "Water type adoption profile - PUT exception test",
                0,
                LocalDate.now().minusDays(8),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(18),
                "Pastoria City Adoption Center",
                AdoptionStatus.PENDING,
                "Testing PUT exception path - will try invalid status"
        );

        // DELETE - Success path
        createAdoption(
                "d88b2339-d24b-435b-9ef0-f477f13235fe",
                "b5c6d7e8-f9a0-b1c2-d3e4-f5a6b7c8d9e0", // creatureId (Breeze)
                "d4e5f6a7-b8c9-d0e1-f2a3-b4c5d6e7f8a9", // customerId (Clay)
                "o5p4q3r2-s1t0-u9v8-w7x6-y5z4a3b2c1d0", // trainingId (Grass Healing)
                "Grass type adoption profile - DELETE success test",
                0,
                LocalDate.now().minusDays(4),
                ProfileStatus.ACTIVE,
                LocalDate.now().plusDays(21),
                "Eterna City Adoption Center",
                AdoptionStatus.PENDING, // Not COMPLETED, so can be deleted
                "Testing DELETE success path - can be deleted"
        );

        // DELETE - Exception path
        createAdoption(
                "55212ccd-f45c-489b-ae44-1d2e4e2906d4",
                "c6d7e8f9-a0b1-c2d3-e4f5-a6b7c8d9e0f1", // creatureId (Dusk)
                "a7b8c9d0-e1f2-a3b4-c5d6-e7f8a9b0c1d2", // customerId (Skyla)
                "l8m7n6o5-p4q3-r2s1-t0u9-v8w7x6y5z4a3", // trainingId (Dark Energy Control)
                "Dark type adoption profile - DELETE exception test",
                1,
                LocalDate.now().minusDays(20),
                ProfileStatus.INACTIVE,
                LocalDate.now().minusDays(5),
                "Virbank Complex Adoption Center",
                AdoptionStatus.COMPLETED, // COMPLETED, so cannot be deleted
                "Testing DELETE exception path - completed adoption cannot be deleted"
        );



        log.info("Sample adoption data loaded successfully.");
    }

//    private void createAdoption(
//            String creatureId,
//            String customerId,
//            String trainingId,
//            String summary,
//            Integer totalAdoptions,
//            LocalDate profileCreationDate,
//            ProfileStatus profileStatus,
//            LocalDate adoptionDate,
//            String adoptionLocation,
//            AdoptionStatus adoptionStatus,
//            String specialNotes) {
//
//        try {
//            AdoptionIdentifier adoptionIdentifier = new AdoptionIdentifier(null);
//
//            Adoption adoption = Adoption.builder()
//                    .adoptionIdentifier(adoptionIdentifier)
//                    .creatureId(creatureId)
//                    .customerId(customerId)
//                    .trainingId(trainingId)
//                    .summary(summary)
//                    .totalAdoptions(totalAdoptions)
//                    .profileCreationDate(profileCreationDate)
//                    .lastUpdated(LocalDateTime.now())
//                    .profileStatus(profileStatus)
//                    .adoptionDate(adoptionDate)
//                    .adoptionLocation(adoptionLocation)
//                    .adoptionStatus(adoptionStatus)
//                    .specialNotes(specialNotes)
//                    .build();
//
//            adoptionRepository.save(adoption);
//            log.debug("Created adoption with ID: {}", adoption.getAdoptionIdentifier().getAdoptionId());
//        } catch (Exception e) {
//            log.error("Error creating adoption record: {}", e.getMessage(), e);
//        }

    private void createAdoption(
            String adoptionId, // Add this parameter
            String creatureId,
            String customerId,
            String trainingId,
            String summary,
            Integer totalAdoptions,
            LocalDate profileCreationDate,
            ProfileStatus profileStatus,
            LocalDate adoptionDate,
            String adoptionLocation,
            AdoptionStatus adoptionStatus,
            String specialNotes) {

        try {
            // Create a custom AdoptionIdentifier with the provided adoptionId
            AdoptionIdentifier adoptionIdentifier = new AdoptionIdentifier(null); // Still needed for the code

            // Use reflection to directly set the adoptionId field
            try {
                java.lang.reflect.Field field = AdoptionIdentifier.class.getDeclaredField("adoptionId");
                field.setAccessible(true);
                field.set(adoptionIdentifier, adoptionId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                log.error("Failed to set adoption ID: {}", e.getMessage());
            }

            Adoption adoption = Adoption.builder()
                    .adoptionIdentifier(adoptionIdentifier)
                    .creatureId(creatureId)
                    .customerId(customerId)
                    .trainingId(trainingId)
                    .summary(summary)
                    .totalAdoptions(totalAdoptions)
                    .profileCreationDate(profileCreationDate)
                    .lastUpdated(LocalDateTime.now())
                    .profileStatus(profileStatus)
                    .adoptionDate(adoptionDate)
                    .adoptionLocation(adoptionLocation)
                    .adoptionStatus(adoptionStatus)
                    .specialNotes(specialNotes)
                    .build();

            adoptionRepository.save(adoption);
            log.debug("Created adoption with ID: {}", adoption.getAdoptionIdentifier().getAdoptionId());
        } catch (Exception e) {
            log.error("Error creating adoption record: {}", e.getMessage(), e);
        }
    }
}