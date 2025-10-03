package com.creatureadoption.adoptions.domainclientlayer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainingResponseModel {
    private String trainingId;
    private String trainingCode;
    private String name;
    private String description;
    private String difficulty;
    private Integer duration;
    private String status;
    private String category;
    private Double price;
    private String location;
}