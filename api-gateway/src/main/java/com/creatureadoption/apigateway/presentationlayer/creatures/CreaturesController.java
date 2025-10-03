package com.creatureadoption.apigateway.presentationlayer.creatures;

import com.creatureadoption.apigateway.businesslayer.creatures.CreaturesService;
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
@RequestMapping("api/v1/creatures")
public class CreaturesController {

    private final CreaturesService creaturesService;
    private static final int UUID_LENGTH = 36;

    public CreaturesController(CreaturesService creaturesService) {
        this.creaturesService = creaturesService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<CreatureResponseModel>> getCreatures(@RequestParam(required = false) Map<String, String> queryParams) {
        if (queryParams.containsKey("creatureId")) {
            String creatureId = queryParams.get("creatureId");
            if (creatureId != null && creatureId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
            }
        }

        List<CreatureResponseModel> creatures = creaturesService.getCreatures(queryParams);
        for(CreatureResponseModel creature : creatures) {
            addSelfLink(creature);
        }

        return ResponseEntity.ok(creatures);
    }

    @GetMapping(
            value = "/{creatureId}",
            produces = "application/json"
    )
    public ResponseEntity<CreatureResponseModel> getCreatureByCreatureId(@PathVariable String creatureId) {
        if (creatureId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
        }

        CreatureResponseModel creature = creaturesService.getCreatureByCreatureId(creatureId);
        addSelfLink(creature);

        return ResponseEntity.ok(creature);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<CreatureResponseModel> addCreature(@RequestBody CreatureRequestModel creatureRequestModel) {
        CreatureResponseModel creature = creaturesService.addCreature(creatureRequestModel);
        addSelfLink(creature);

        return ResponseEntity.status(HttpStatus.CREATED).body(creature);
    }

    @PutMapping(
            value = "/{creatureId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<CreatureResponseModel> updateCreature(@RequestBody CreatureRequestModel creatureRequestModel, @PathVariable String creatureId) {
        if (creatureId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
        }

        CreatureResponseModel creature = creaturesService.updateCreature(creatureRequestModel, creatureId);
        addSelfLink(creature);

        return ResponseEntity.ok(creature);
    }

    @DeleteMapping(
            value = "/{creatureId}"
    )
    public ResponseEntity<Void> deleteCreature(@PathVariable String creatureId) {
        if (creatureId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
        }

        creaturesService.removeCreature(creatureId);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(CreatureResponseModel creature) {
        creature.add(
                linkTo(methodOn(CreaturesController.class)
                        .getCreatureByCreatureId(creature.getCreatureId()))
                        .withSelfRel()
        );

        creature.add(
                linkTo(methodOn(CreaturesController.class)
                        .getCreatures(Map.of()))
                        .withRel("allCreatures")
        );
    }
}