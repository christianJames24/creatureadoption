package com.creatureadoption.creatures.presentationlayer;

import com.creatureadoption.creatures.businesslayer.CreatureService;
import com.creatureadoption.creatures.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/creatures")
public class CreatureController {

    private final CreatureService creatureService;
    private static final int UUID_LENGTH = 36;

    public CreatureController(CreatureService creatureService) {
        this.creatureService = creatureService;
    }

    @GetMapping
    public ResponseEntity<List<CreatureResponseModel>> getCreatures(
            @RequestParam(required = false) Map<String, String> queryParams) {

        if (queryParams.containsKey("creatureId")) {
            String creatureId = queryParams.get("creatureId");
            if (creatureId != null && creatureId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
            }
        }
        return ResponseEntity.ok().body(creatureService.getCreatures(queryParams));
    }

    @GetMapping("/{creatureId}")
    public ResponseEntity<CreatureResponseModel> getCreatureByCreatureId(@PathVariable String creatureId) {
        if (creatureId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
        }
        return ResponseEntity.ok().body(creatureService.getCreatureByCreatureId(creatureId));
    }

    @PostMapping()
    public ResponseEntity<CreatureResponseModel> addCreature(@RequestBody CreatureRequestModel creatureRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creatureService.addCreature(creatureRequestModel));
    }

    @PutMapping("/{creatureId}")
    public ResponseEntity<CreatureResponseModel> updateCreature(@RequestBody CreatureRequestModel creatureRequestModel, @PathVariable String creatureId) {
        if (creatureId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
        }
        return ResponseEntity.ok().body(creatureService.updateCreature(creatureRequestModel, creatureId));
    }

    @DeleteMapping("/{creatureId}")
    public ResponseEntity<Void> deleteCreature(@PathVariable String creatureId) {
        if (creatureId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid creatureId provided: " + creatureId);
        }
        creatureService.removeCreature(creatureId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}