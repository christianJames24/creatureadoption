package com.creatureadoption.apigateway.businesslayer.trainings;

import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingRequestModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingResponseModel;

import java.util.List;
import java.util.Map;

public interface TrainingsService {
    List<TrainingResponseModel> getTrainings(Map<String, String> queryParams);
    TrainingResponseModel getTrainingByTrainingId(String trainingId);
    TrainingResponseModel addTraining(TrainingRequestModel trainingRequestModel);
    TrainingResponseModel updateTraining(TrainingRequestModel trainingRequestModel, String trainingId);
    void removeTraining(String trainingId);
}