package com.creatureadoption.adoptions.presentationlayer;

import com.creatureadoption.adoptions.dataaccesslayer.AdoptionStatus;
import com.creatureadoption.adoptions.dataaccesslayer.CreatureStatus;
import com.creatureadoption.adoptions.dataaccesslayer.ProfileStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class AdoptionResponseModel extends RepresentationModel<AdoptionResponseModel> {

    String adoptionId;
    String adoptionCode;
    String summary;
    Integer totalAdoptions;
    LocalDate profileCreationDate;
    LocalDateTime lastUpdated;
    ProfileStatus profileStatus;
    LocalDate adoptionDate;
    String adoptionLocation;
    AdoptionStatus adoptionStatus;
    String specialNotes;

    // Customer details
    String customerId;
    String customerFirstName;
    String customerLastName;

    // Creature details
    String creatureId;
    String creatureName;
    String creatureSpecies;
    CreatureStatus creatureStatus;

    // Training details
    String trainingId;
    String trainingName;
    String trainingLocation;
}