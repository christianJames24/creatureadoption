package com.creatureadoption.apigateway.businesslayer.trainings;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.trainings.TrainingsServiceClient;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingRequestModel;
import com.creatureadoption.apigateway.presentationlayer.trainings.TrainingResponseModel;
import com.creatureadoption.apigateway.utils.exceptions.EntityInUseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TrainingsServiceImpl implements TrainingsService {

    private final TrainingsServiceClient trainingsServiceClient;
    private final AdoptionsServiceClient adoptionsServiceClient;

    public TrainingsServiceImpl(TrainingsServiceClient trainingsServiceClient, AdoptionsServiceClient adoptionsServiceClient) {
        this.trainingsServiceClient = trainingsServiceClient;
        this.adoptionsServiceClient = adoptionsServiceClient;
    }

    @Override
    public List<TrainingResponseModel> getTrainings(Map<String, String> queryParams) {
        return trainingsServiceClient.getTrainings(queryParams);
    }

    @Override
    public TrainingResponseModel getTrainingByTrainingId(String trainingId) {
        return trainingsServiceClient.getTrainingByTrainingId(trainingId);
    }

    @Override
    public TrainingResponseModel addTraining(TrainingRequestModel trainingRequestModel) {
        return trainingsServiceClient.addTraining(trainingRequestModel);
    }

    @Override
    public TrainingResponseModel updateTraining(TrainingRequestModel trainingRequestModel, String trainingId) {
        return trainingsServiceClient.updateTraining(trainingRequestModel, trainingId);
    }

    @Override
    public void removeTraining(String trainingId) {
        // Get all adoptions and check if any use this training
        List<AdoptionResponseModel> allAdoptions = adoptionsServiceClient.getAdoptions(Map.of());

        boolean trainingInUse = allAdoptions.stream()
                .anyMatch(adoption -> adoption.getTrainingId() != null &&
                        adoption.getTrainingId().equals(trainingId));

        if (trainingInUse) {
            throw new EntityInUseException("Cannot delete training with ID: " + trainingId +
                    " because it is used in existing adoptions");
        }

        trainingsServiceClient.removeTraining(trainingId);
    }
}