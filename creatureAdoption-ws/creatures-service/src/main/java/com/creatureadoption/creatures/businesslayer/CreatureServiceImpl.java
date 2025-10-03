package com.creatureadoption.creatures.businesslayer;

import com.creatureadoption.creatures.dataaccesslayer.Creature;
import com.creatureadoption.creatures.dataaccesslayer.CreatureIdentifier;
import com.creatureadoption.creatures.dataaccesslayer.CreatureRepository;
import com.creatureadoption.creatures.dataaccesslayer.CreatureTraits;
import com.creatureadoption.creatures.mappinglayer.CreatureRequestMapper;
import com.creatureadoption.creatures.mappinglayer.CreatureResponseMapper;
import com.creatureadoption.creatures.presentationlayer.CreatureRequestModel;
import com.creatureadoption.creatures.presentationlayer.CreatureResponseModel;
import com.creatureadoption.creatures.utils.exceptions.DuplicateCreatureNameException;
import com.creatureadoption.creatures.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CreatureServiceImpl implements CreatureService {

    private final CreatureRepository creatureRepository;
    private final CreatureResponseMapper creatureResponseMapper;
    private final CreatureRequestMapper creatureRequestMapper;

    public CreatureServiceImpl(CreatureRepository creatureRepository, CreatureResponseMapper creatureResponseMapper, CreatureRequestMapper creatureRequestMapper) {
        this.creatureRepository = creatureRepository;
        this.creatureResponseMapper = creatureResponseMapper;
        this.creatureRequestMapper = creatureRequestMapper;
    }

    @Override
    public List<CreatureResponseModel> getCreatures(Map<String, String> queryParams) {
        List<Creature> creatures = creatureRepository.findAll();

        String creatureId = queryParams.get("creatureId");
        String name = queryParams.get("name");
        String species = queryParams.get("species");
        String type = queryParams.get("type");
        String rarity = queryParams.get("rarity");
        String status = queryParams.get("status");

        if (type != null && !type.isEmpty()) {
            creatures = creatures.stream()
                    .filter(c -> c.getType() != null &&
                            c.getType().toString().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        }
        if (rarity != null && !rarity.isEmpty()) {
            creatures = creatures.stream()
                    .filter(c -> c.getRarity() != null &&
                            c.getRarity().toString().equalsIgnoreCase(rarity))
                    .collect(Collectors.toList());
        }
        if (status != null && !status.isEmpty()) {
            creatures = creatures.stream()
                    .filter(c -> c.getStatus() != null &&
                            c.getStatus().toString().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }

        return creatureResponseMapper.entityListToResponseModelList(creatures);
    }

    @Override
    public CreatureResponseModel getCreatureByCreatureId(String creatureId) {
        Creature creature = creatureRepository.findByCreatureIdentifier_CreatureId(creatureId);

        if (creature == null) {
            throw new NotFoundException("Provided creatureId not found: " + creatureId);
        }
        return creatureResponseMapper.entityToResponseModel(creature);
    }

    @Override
    public CreatureResponseModel addCreature(CreatureRequestModel creatureRequestModel) {
        List<Creature> existingCreatures = creatureRepository.findAll().stream() //microservice specific exception
                .filter(c -> c.getName().equalsIgnoreCase(creatureRequestModel.getName()))
                .collect(Collectors.toList());

        if (!existingCreatures.isEmpty()) {
            throw new DuplicateCreatureNameException("A creature with name " + creatureRequestModel.getName() + " already exists");
        }

        CreatureTraits traits = new CreatureTraits(
                creatureRequestModel.getStrength(),
                creatureRequestModel.getIntelligence(),
                creatureRequestModel.getAgility(),
                creatureRequestModel.getTemperament()
        );

        Creature creature = creatureRequestMapper.requestModelToEntity(creatureRequestModel, new CreatureIdentifier(), traits);

        creature.setCreatureTraits(traits);
        return creatureResponseMapper.entityToResponseModel(creatureRepository.save(creature));
    }

    @Override
    public CreatureResponseModel updateCreature(CreatureRequestModel creatureRequestModel, String creatureId) {
        // Fixed code:
        List<Creature> existingCreatures = creatureRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(creatureRequestModel.getName()))
                .filter(c -> !c.getCreatureIdentifier().getCreatureId().equals(creatureId)) // Add this line
                .collect(Collectors.toList());

        if (!existingCreatures.isEmpty()) {
            throw new DuplicateCreatureNameException("A creature with name " + creatureRequestModel.getName() + " already exists");
        }

        Creature existingCreature = creatureRepository.findByCreatureIdentifier_CreatureId(creatureId);

        if (existingCreature == null) {
            throw new NotFoundException("Provided creatureId not found: " + creatureId);
        }

        CreatureTraits traits = new CreatureTraits(
                creatureRequestModel.getStrength(),
                creatureRequestModel.getIntelligence(),
                creatureRequestModel.getAgility(),
                creatureRequestModel.getTemperament()
        );

        Creature updatedCreature = creatureRequestMapper.requestModelToEntity(creatureRequestModel,
                existingCreature.getCreatureIdentifier(), traits);
        updatedCreature.setId(existingCreature.getId());

        Creature response = creatureRepository.save(updatedCreature);
        return creatureResponseMapper.entityToResponseModel(response);
    }

    @Override
    public void removeCreature(String creatureId) {
        Creature existingCreature = creatureRepository.findByCreatureIdentifier_CreatureId(creatureId);

        if (existingCreature == null) {
            throw new NotFoundException("Provided creatureId not found: " + creatureId);
        }

        creatureRepository.delete(existingCreature);
    }
}