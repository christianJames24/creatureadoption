package com.creatureadoption.creatures.presentationlayer;

import com.creatureadoption.creatures.dataaccesslayer.CreatureStatus;
import com.creatureadoption.creatures.dataaccesslayer.CreatureType;
import com.creatureadoption.creatures.dataaccesslayer.Rarity;
import com.creatureadoption.creatures.dataaccesslayer.Temperament;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class CreatureResponseModel extends RepresentationModel<CreatureResponseModel> {

    String creatureId;
    String registrationCode;
    String name;
    String species;
    CreatureType type;
    Rarity rarity;
    Integer level;
    Integer age;
    Integer health;
    Integer experience;
    CreatureStatus status;
    Integer strength;
    Integer intelligence;
    Integer agility;
    Temperament temperament;
}