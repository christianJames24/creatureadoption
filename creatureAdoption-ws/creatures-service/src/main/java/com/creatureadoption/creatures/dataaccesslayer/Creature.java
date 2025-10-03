package com.creatureadoption.creatures.dataaccesslayer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name="creatures")
@Data
@NoArgsConstructor
public class Creature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private CreatureIdentifier creatureIdentifier;

    private String name;
    private String species;

    @Enumerated(EnumType.STRING)
    private CreatureType type;

    @Enumerated(EnumType.STRING)
    private Rarity rarity;

    private Integer level;
    private Integer age;
    private Integer health;
    private Integer experience;

    @Enumerated(EnumType.STRING)
    private CreatureStatus status;

    @Embedded
    private CreatureTraits creatureTraits;

    public Creature(@NotNull String name, @NotNull String species, @NotNull CreatureType type,
                    @NotNull Rarity rarity, @NotNull Integer level, @NotNull Integer age,
                    @NotNull Integer health, @NotNull Integer experience, @NotNull CreatureStatus status,
                    @NotNull CreatureTraits creatureTraits) {
        this.creatureIdentifier = new CreatureIdentifier();
        this.name = name;
        this.species = species;
        this.type = type;
        this.rarity = rarity;
        this.level = level;
        this.age = age;
        this.health = health;
        this.experience = experience;
        this.status = status;
        this.creatureTraits = creatureTraits;
    }
}