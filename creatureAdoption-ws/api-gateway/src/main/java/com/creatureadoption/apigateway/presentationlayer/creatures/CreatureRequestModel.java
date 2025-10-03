package com.creatureadoption.apigateway.presentationlayer.creatures;


import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureType;
import com.creatureadoption.apigateway.domainclientlayer.creatures.Rarity;
import com.creatureadoption.apigateway.domainclientlayer.creatures.Temperament;
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