package com.creatureadoption.adoptions.presentationlayer;

import com.creatureadoption.adoptions.businesslayer.AdoptionService;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/adoptions")
public class AdoptionController {

    private final AdoptionService adoptionService;
    private static final int UUID_LENGTH = 36;

    public AdoptionController(AdoptionService adoptionService) {
        this.adoptionService = adoptionService;
    }

    @GetMapping
    public ResponseEntity<List<AdoptionResponseModel>> getAdoptions(
            @RequestParam(required = false) Map<String, String> queryParams) {

        if (queryParams.containsKey("adoptionId")) {
            String adoptionId = queryParams.get("adoptionId");
            if (adoptionId != null && adoptionId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
            }
        }

        if (queryParams.containsKey("customerId")) {
            String customerId = queryParams.get("customerId");
            if (customerId != null && customerId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid customerId provided: " + customerId);
            }
        }

        if (queryParams.containsKey("creatureId")) {
            String creatureId = queryParams.get("creatureId");
            if (creatureId != null && creatureId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
            }
        }

        return ResponseEntity.ok().body(adoptionService.getAdoptions(queryParams));
    }

    @GetMapping("/{adoptionId}")
    public ResponseEntity<AdoptionResponseModel> getAdoptionByAdoptionId(@PathVariable String adoptionId) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }
        return ResponseEntity.ok().body(adoptionService.getAdoptionByAdoptionId(adoptionId));
    }

    @PostMapping()
    public ResponseEntity<AdoptionResponseModel> addAdoption(@RequestBody AdoptionRequestModel adoptionRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adoptionService.addAdoption(adoptionRequestModel));
    }

    @PutMapping("/{adoptionId}")
    public ResponseEntity<AdoptionResponseModel> updateAdoption(
            @RequestBody AdoptionRequestModel adoptionRequestModel,
            @PathVariable String adoptionId) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }
        return ResponseEntity.ok().body(adoptionService.updateAdoption(adoptionRequestModel, adoptionId));
    }

    @PatchMapping("/{adoptionId}/status/{status}")
    public ResponseEntity<AdoptionResponseModel> updateAdoptionStatus(
            @PathVariable String adoptionId,
            @PathVariable String status) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }
        return ResponseEntity.ok().body(adoptionService.updateAdoptionStatus(adoptionId, status));
    }

    @DeleteMapping("/{adoptionId}")
    public ResponseEntity<Void> deleteAdoption(@PathVariable String adoptionId) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }
        adoptionService.removeAdoption(adoptionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}