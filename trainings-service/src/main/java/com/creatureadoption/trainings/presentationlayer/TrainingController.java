package com.creatureadoption.trainings.presentationlayer;

import com.creatureadoption.trainings.businesslayer.TrainingService;
import com.creatureadoption.trainings.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/trainings")
public class TrainingController {

    private final TrainingService trainingService;
    private static final int UUID_LENGTH = 36;

    public TrainingController(TrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping
    public ResponseEntity<List<TrainingResponseModel>> getTrainings(
            @RequestParam(required = false) Map<String, String> queryParams) {

        if (queryParams.containsKey("trainingId")) {
            String trainingId = queryParams.get("trainingId");
            if (trainingId != null && trainingId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
            }
        }
        return ResponseEntity.ok().body(trainingService.getTrainings(queryParams));
    }

    @GetMapping("/{trainingId}")
    public ResponseEntity<TrainingResponseModel> getTrainingByTrainingId(@PathVariable String trainingId) {
        if (trainingId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
        }
        return ResponseEntity.ok().body(trainingService.getTrainingByTrainingId(trainingId));
    }

    @PostMapping()
    public ResponseEntity<TrainingResponseModel> addTraining(@RequestBody TrainingRequestModel trainingRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingService.addTraining(trainingRequestModel));
    }

    @PutMapping("/{trainingId}")
    public ResponseEntity<TrainingResponseModel> updateTraining(@RequestBody TrainingRequestModel trainingRequestModel, @PathVariable String trainingId) {
        if (trainingId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
        }
        return ResponseEntity.ok().body(trainingService.updateTraining(trainingRequestModel, trainingId));
    }

    @DeleteMapping("/{trainingId}")
    public ResponseEntity<Void> deleteTraining(@PathVariable String trainingId) {
        if (trainingId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid trainingId provided: " + trainingId);
        }
        trainingService.removeTraining(trainingId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}