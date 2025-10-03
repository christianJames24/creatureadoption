package com.creatureadoption.trainings.businesslayer;

import com.creatureadoption.trainings.dataaccesslayer.Training;
import com.creatureadoption.trainings.dataaccesslayer.TrainingIdentifier;
import com.creatureadoption.trainings.dataaccesslayer.TrainingRepository;
import com.creatureadoption.trainings.mappinglayer.TrainingRequestMapper;
import com.creatureadoption.trainings.mappinglayer.TrainingResponseMapper;
import com.creatureadoption.trainings.presentationlayer.TrainingRequestModel;
import com.creatureadoption.trainings.presentationlayer.TrainingResponseModel;
import com.creatureadoption.trainings.utils.exceptions.DuplicateTrainingNameException;
import com.creatureadoption.trainings.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TrainingResponseMapper trainingResponseMapper;
    private final TrainingRequestMapper trainingRequestMapper;

    public TrainingServiceImpl(TrainingRepository trainingRepository, TrainingResponseMapper trainingResponseMapper, TrainingRequestMapper trainingRequestMapper) {
        this.trainingRepository = trainingRepository;
        this.trainingResponseMapper = trainingResponseMapper;
        this.trainingRequestMapper = trainingRequestMapper;
    }

    @Override
    public List<TrainingResponseModel> getTrainings(Map<String, String> queryParams) {
        List<Training> trainings = trainingRepository.findAll();

        String trainingId = queryParams.get("trainingId");
        String name = queryParams.get("name");
        String difficulty = queryParams.get("difficulty");
        String category = queryParams.get("category");
        String status = queryParams.get("status");

        if (difficulty != null && !difficulty.isEmpty()) {
            trainings = trainings.stream()
                    .filter(t -> t.getDifficulty() != null &&
                            t.getDifficulty().toString().equalsIgnoreCase(difficulty))
                    .collect(Collectors.toList());
        }
        if (category != null && !category.isEmpty()) {
            trainings = trainings.stream()
                    .filter(t -> t.getCategory() != null &&
                            t.getCategory().toString().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            trainings = trainings.stream()
                    .filter(t -> t.getStatus() != null &&
                            t.getStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        return trainingResponseMapper.entityListToResponseModelList(trainings);
    }

    @Override
    public TrainingResponseModel getTrainingByTrainingId(String trainingId) {
        Training training = trainingRepository.findByTrainingIdentifier_TrainingId(trainingId);

        if (training == null) {
            throw new NotFoundException("Provided trainingId not found: " + trainingId);
        }
        return trainingResponseMapper.entityToResponseModel(training);
    }

    @Override
    public TrainingResponseModel addTraining(TrainingRequestModel trainingRequestModel) {
        List<Training> existingTrainings = trainingRepository.findAll().stream() //subdomamin specific
                .filter(t -> t.getName().equalsIgnoreCase(trainingRequestModel.getName()))
                .collect(Collectors.toList());

        if (!existingTrainings.isEmpty()) {
            throw new DuplicateTrainingNameException("A training with name " + trainingRequestModel.getName() + " already exists");
        }

        Training training = trainingRequestMapper.requestModelToEntity(trainingRequestModel, new TrainingIdentifier());
        return trainingResponseMapper.entityToResponseModel(trainingRepository.save(training));
    }

    @Override
    public TrainingResponseModel updateTraining(TrainingRequestModel trainingRequestModel, String trainingId) {
        List<Training> existingTrainings = trainingRepository.findAll().stream() //subdomamin specific
                .filter(t -> t.getName().equalsIgnoreCase(trainingRequestModel.getName()))
                .collect(Collectors.toList());

        if (!existingTrainings.isEmpty()) {
            throw new DuplicateTrainingNameException("A training with name " + trainingRequestModel.getName() + " already exists");
        }

        Training existingTraining = trainingRepository.findByTrainingIdentifier_TrainingId(trainingId);

        if (existingTraining == null) {
            throw new NotFoundException("Provided trainingId not found: " + trainingId);
        }

        Training updatedTraining = trainingRequestMapper.requestModelToEntity(trainingRequestModel,
                existingTraining.getTrainingIdentifier());
        updatedTraining.setId(existingTraining.getId());

        Training response = trainingRepository.save(updatedTraining);
        return trainingResponseMapper.entityToResponseModel(response);
    }

    @Override
    public void removeTraining(String trainingId) {
        Training existingTraining = trainingRepository.findByTrainingIdentifier_TrainingId(trainingId);

        if (existingTraining == null) {
            throw new NotFoundException("Provided trainingId not found: " + trainingId);
        }

        trainingRepository.delete(existingTraining);
    }
}