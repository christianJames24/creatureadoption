package com.creatureadoption.apigateway.presentationlayer.trainings;

import com.creatureadoption.apigateway.businesslayer.trainings.TrainingsService;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("api/v1/trainings")
public class TrainingsController {

    private final TrainingsService trainingsService;
    private static final int UUID_LENGTH = 36;

    public TrainingsController(TrainingsService trainingsService) {
        this.trainingsService = trainingsService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<TrainingResponseModel>> getTrainings(@RequestParam(required = false) Map<String, String> queryParams) {
        if (queryParams.containsKey("trainingId")) {
            String trainingId = queryParams.get("trainingId");
            if (trainingId != null && trainingId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
            }
        }

        List<TrainingResponseModel> trainings = trainingsService.getTrainings(queryParams);
        for(TrainingResponseModel training : trainings) {
            addSelfLink(training);
        }

        return ResponseEntity.ok(trainings);
    }

    @GetMapping(
            value = "/{trainingId}",
            produces = "application/json"
    )
    public ResponseEntity<TrainingResponseModel> getTrainingByTrainingId(@PathVariable String trainingId) {
        if (trainingId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
        }

        TrainingResponseModel training = trainingsService.getTrainingByTrainingId(trainingId);
        addSelfLink(training);

        return ResponseEntity.ok(training);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<TrainingResponseModel> addTraining(@RequestBody TrainingRequestModel trainingRequestModel) {
        TrainingResponseModel training = trainingsService.addTraining(trainingRequestModel);
        addSelfLink(training);

        return ResponseEntity.status(HttpStatus.CREATED).body(training);
    }

    @PutMapping(
            value = "/{trainingId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<TrainingResponseModel> updateTraining(@RequestBody TrainingRequestModel trainingRequestModel, @PathVariable String trainingId) {
        if (trainingId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
        }

        TrainingResponseModel training = trainingsService.updateTraining(trainingRequestModel, trainingId);
        addSelfLink(training);

        return ResponseEntity.ok(training);
    }

    @DeleteMapping(
            value = "/{trainingId}"
    )
    public ResponseEntity<Void> deleteTraining(@PathVariable String trainingId) {
        if (trainingId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
        }

        trainingsService.removeTraining(trainingId);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(TrainingResponseModel training) {
        training.add(
                linkTo(methodOn(TrainingsController.class)
                        .getTrainingByTrainingId(training.getTrainingId()))
                        .withSelfRel()
        );

        training.add(
                linkTo(methodOn(TrainingsController.class)
                        .getTrainings(Map.of()))
                        .withRel("allTrainings")
        );
    }
}