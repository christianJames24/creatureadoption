package com.creatureadoption.adoptions.dataaccesslayer;

import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "adoptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adoption {

    @Id
    private String id;
    private AdoptionIdentifier adoptionIdentifier;

    private String summary;
    private Integer totalAdoptions;
    private LocalDate profileCreationDate;
    private LocalDateTime lastUpdated;
    private ProfileStatus profileStatus;
    private LocalDate adoptionDate;
    private String adoptionLocation;
    private AdoptionStatus adoptionStatus;
    private String specialNotes;

    // ids from other subdomains as aggregate root
    private String customerId;
    private String creatureId;
    private String trainingId;

    /**
     * Updates the adoption status and determines the appropriate creature status that should result.
     * This enforces the invariant that adoption status and creature status must be consistent.
     *
     * @param newStatus The new adoption status to set
     * @return The creature status that should be applied as a result of this change
     */
    public CreatureStatus updateAdoptionStatus(AdoptionStatus newStatus) {
        AdoptionStatus previousStatus = this.adoptionStatus;
        this.adoptionStatus = newStatus;
        this.lastUpdated = LocalDateTime.now();

        // Determine the required creature status based on adoption status
        switch (newStatus) {
            case PENDING:
                return CreatureStatus.ADOPTION_PENDING;
            case APPROVED:
                return CreatureStatus.RESERVED;
            case COMPLETED:
                return CreatureStatus.ADOPTED;
            case CANCELLED:
            case RETURNED:
                return CreatureStatus.AVAILABLE;
            default:
                throw new InvalidInputException("Unsupported adoption status: " + newStatus);
        }
    }

    /**
     * Validates if this adoption can be deleted.
     * Enforces the invariant that completed adoptions cannot be deleted.
     *
     * @throws InvalidInputException if the adoption cannot be deleted
     */
    public void validateDeletion() {
        if (this.adoptionStatus == AdoptionStatus.COMPLETED) {
            throw new InvalidInputException("Cannot remove a completed adoption. Please update status to CANCELLED or RETURNED first.");
        }
    }

    /**
     * Validates if this customer has reached the maximum allowed adoptions.
     *
     * @param currentCompletedAdoptions The number of completed adoptions for this customer
     * @param maxAllowedAdoptions The maximum number of adoptions allowed per customer
     * @throws InvalidInputException if the customer has reached the maximum allowed adoptions
     */
    public static void validateCustomerAdoptionLimit(int currentCompletedAdoptions, int maxAllowedAdoptions) {
        if (currentCompletedAdoptions >= maxAllowedAdoptions) {
            throw new InvalidInputException("Customer has reached the maximum limit of " + maxAllowedAdoptions + " adoptions");
        }
    }
}