package com.creatureadoption.creatures.presentationlayer;

import com.creatureadoption.creatures.dataaccesslayer.CreatureStatus;
import com.creatureadoption.creatures.dataaccesslayer.CreatureType;
import com.creatureadoption.creatures.dataaccesslayer.Rarity;
import com.creatureadoption.creatures.dataaccesslayer.Temperament;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreatureRequestModel {

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