package com.creatureadoption.apigateway.presentationlayer.creatures;


import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureType;
import com.creatureadoption.apigateway.domainclientlayer.creatures.Rarity;
import com.creatureadoption.apigateway.domainclientlayer.creatures.Temperament;
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