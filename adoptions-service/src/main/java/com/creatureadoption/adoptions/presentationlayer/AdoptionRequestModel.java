package com.creatureadoption.adoptions.presentationlayer;

import com.creatureadoption.adoptions.dataaccesslayer.AdoptionStatus;
import com.creatureadoption.adoptions.dataaccesslayer.ProfileStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AdoptionRequestModel {

    String summary;
    Integer totalAdoptions;
    LocalDate profileCreationDate;
    ProfileStatus profileStatus;
    LocalDate adoptionDate;
    String adoptionLocation;
    AdoptionStatus adoptionStatus;
    String specialNotes;
    String customerId;
    String creatureId;
    String trainingId;
}