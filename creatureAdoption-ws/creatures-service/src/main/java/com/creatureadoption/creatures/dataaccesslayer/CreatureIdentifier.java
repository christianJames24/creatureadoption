package com.creatureadoption.creatures.dataaccesslayer;

import jakarta.persistence.Embeddable;
import lombok.Getter;

import java.util.UUID;

@Embeddable
@Getter
public class CreatureIdentifier {

    private String creatureId;
    private String registrationCode;

    public CreatureIdentifier() {
        this.creatureId = UUID.randomUUID().toString();
        this.registrationCode = "REG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public CreatureIdentifier(String creatureId, String registrationCode) {
        this.creatureId = creatureId;
        this.registrationCode = registrationCode;
    }
}