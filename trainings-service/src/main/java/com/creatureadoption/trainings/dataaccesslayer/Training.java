package com.creatureadoption.trainings.dataaccesslayer;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Table(name="trainings")
@Data
@NoArgsConstructor
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Embedded
    private TrainingIdentifier trainingIdentifier;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private TrainingStatus status;

    @Enumerated(EnumType.STRING)
    private TrainingCategory category;

    private Double price;
    private String location;

    public Training(@NotNull String name, @NotNull String description, @NotNull Difficulty difficulty,
                    @NotNull Integer duration, @NotNull TrainingStatus status, @NotNull TrainingCategory category,
                    @NotNull Double price, @NotNull String location) {
        this.trainingIdentifier = new TrainingIdentifier();
        this.name = name;
        this.description = description;
        this.difficulty = difficulty;
        this.duration = duration;
        this.status = status;
        this.category = category;
        this.price = price;
        this.location = location;
    }
}