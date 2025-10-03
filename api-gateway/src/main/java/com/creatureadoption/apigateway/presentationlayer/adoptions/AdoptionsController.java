package com.creatureadoption.apigateway.presentationlayer.adoptions;

import com.creatureadoption.apigateway.businesslayer.adoptions.AdoptionsService;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("api/v1/adoptions")
public class AdoptionsController {

    private final AdoptionsService adoptionsService;
    private static final int UUID_LENGTH = 36;

    public AdoptionsController(AdoptionsService adoptionsService) {
        this.adoptionsService = adoptionsService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<AdoptionResponseModel>> getAdoptions(@RequestParam(required = false) Map<String, String> queryParams) {
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

        List<AdoptionResponseModel> adoptions = adoptionsService.getAdoptions(queryParams);
        for(AdoptionResponseModel adoption : adoptions) {
            addSelfLink(adoption);
        }

        return ResponseEntity.ok(adoptions);
    }

    @GetMapping(
            value = "/{adoptionId}",
            produces = "application/json"
    )
    public ResponseEntity<AdoptionResponseModel> getAdoptionByAdoptionId(@PathVariable String adoptionId) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }

        AdoptionResponseModel adoption = adoptionsService.getAdoptionByAdoptionId(adoptionId);
        addSelfLink(adoption);

        return ResponseEntity.ok(adoption);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AdoptionResponseModel> addAdoption(@RequestBody AdoptionRequestModel adoptionRequestModel) {
        AdoptionResponseModel adoption = adoptionsService.addAdoption(adoptionRequestModel);
        addSelfLink(adoption);

        return ResponseEntity.status(HttpStatus.CREATED).body(adoption);
    }

    @PutMapping(
            value = "/{adoptionId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<AdoptionResponseModel> updateAdoption(@RequestBody AdoptionRequestModel adoptionRequestModel, @PathVariable String adoptionId) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }

        AdoptionResponseModel adoption = adoptionsService.updateAdoption(adoptionRequestModel, adoptionId);
        addSelfLink(adoption);

        return ResponseEntity.ok(adoption);
    }

    @PatchMapping(
            value = "/{adoptionId}/status/{status}",
            produces = "application/json"
    )
    public ResponseEntity<AdoptionResponseModel> updateAdoptionStatus(@PathVariable String adoptionId, @PathVariable String status) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }

        AdoptionResponseModel adoption = adoptionsService.updateAdoptionStatus(adoptionId, status);
        addSelfLink(adoption);

        return ResponseEntity.ok(adoption);
    }

    @DeleteMapping(
            value = "/{adoptionId}"
    )
    public ResponseEntity<Void> deleteAdoption(@PathVariable String adoptionId) {
        if (adoptionId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid adoptionId provided: " + adoptionId);
        }

        adoptionsService.removeAdoption(adoptionId);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(AdoptionResponseModel adoption) {
        adoption.add(
                linkTo(methodOn(AdoptionsController.class)
                        .getAdoptionByAdoptionId(adoption.getAdoptionId()))
                        .withSelfRel()
        );

        adoption.add(
                linkTo(methodOn(AdoptionsController.class)
                        .getAdoptions(Map.of()))
                        .withRel("allAdoptions")
        );
    }
}