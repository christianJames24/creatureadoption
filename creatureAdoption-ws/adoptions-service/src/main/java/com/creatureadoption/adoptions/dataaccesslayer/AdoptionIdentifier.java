package com.creatureadoption.adoptions.dataaccesslayer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionIdentifier {

    private String adoptionId;
    private String adoptionCode;

    public AdoptionIdentifier(String adoptionCode) {
        this.adoptionId = UUID.randomUUID().toString();
        this.adoptionCode = adoptionCode != null ? adoptionCode : "ADO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}