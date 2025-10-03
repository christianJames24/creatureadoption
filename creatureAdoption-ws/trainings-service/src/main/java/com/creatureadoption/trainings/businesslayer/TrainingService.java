package com.creatureadoption.trainings.businesslayer;

import com.creatureadoption.trainings.presentationlayer.TrainingRequestModel;
import com.creatureadoption.trainings.presentationlayer.TrainingResponseModel;

import java.util.List;
import java.util.Map;

public interface TrainingService {

    List<TrainingResponseModel> getTrainings(Map<String, String> queryParams);
    TrainingResponseModel getTrainingByTrainingId(String trainingId);
    TrainingResponseModel addTraining(TrainingRequestModel trainingRequestModel);
    TrainingResponseModel updateTraining(TrainingRequestModel updatedTraining, String trainingId);
    void removeTraining(String trainingId);
}