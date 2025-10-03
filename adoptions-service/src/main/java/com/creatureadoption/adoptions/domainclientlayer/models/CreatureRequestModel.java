package com.creatureadoption.adoptions.domainclientlayer.models;

import com.creatureadoption.adoptions.dataaccesslayer.CreatureStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatureRequestModel {
    private String name;
    private String species;
    private String type;
    private String rarity;
    private Integer level;
    private Integer age;
    private Integer health;
    private Integer experience;
    private CreatureStatus status;
    private Integer strength;
    private Integer intelligence;
    private Integer agility;
    private String temperament;
}