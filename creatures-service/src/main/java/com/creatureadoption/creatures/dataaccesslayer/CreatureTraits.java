package com.creatureadoption.creatures.dataaccesslayer;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Objects;

@Embeddable
@NoArgsConstructor
@Getter
public class CreatureTraits {

    private Integer strength;
    private Integer intelligence;
    private Integer agility;

    @Enumerated(EnumType.STRING)
    private Temperament temperament;

    public CreatureTraits(@NotNull Integer strength, @NotNull Integer intelligence,
                          @NotNull Integer agility, @NotNull Temperament temperament) {
        Objects.requireNonNull(this.strength = strength);
        Objects.requireNonNull(this.intelligence = intelligence);
        Objects.requireNonNull(this.agility = agility);
        Objects.requireNonNull(this.temperament = temperament);
    }
}