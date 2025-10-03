package com.creatureadoption.adoptions.businesslayer;

import com.creatureadoption.adoptions.dataaccesslayer.*;
import com.creatureadoption.adoptions.domainclientlayer.CreatureServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.CustomerServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.TrainingServiceClient;
import com.creatureadoption.adoptions.domainclientlayer.models.CreatureResponseModel;
import com.creatureadoption.adoptions.domainclientlayer.models.CustomerResponseModel;
import com.creatureadoption.adoptions.domainclientlayer.models.TrainingResponseModel;
import com.creatureadoption.adoptions.mappinglayer.AdoptionRequestMapper;
import com.creatureadoption.adoptions.mappinglayer.AdoptionResponseMapper;
import com.creatureadoption.adoptions.presentationlayer.AdoptionRequestModel;
import com.creatureadoption.adoptions.presentationlayer.AdoptionResponseModel;
import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AdoptionServiceImpl implements AdoptionService {

    private static final int MAX_ADOPTIONS_PER_CUSTOMER = 2;

    private final AdoptionRepository adoptionRepository;
    private final AdoptionResponseMapper adoptionResponseMapper;
    private final AdoptionRequestMapper adoptionRequestMapper;
    private final CustomerServiceClient customerServiceClient;
    private final CreatureServiceClient creatureServiceClient;
    private final TrainingServiceClient trainingServiceClient;

    @Autowired
    public AdoptionServiceImpl(AdoptionRepository adoptionRepository,
                               AdoptionResponseMapper adoptionResponseMapper,
                               AdoptionRequestMapper adoptionRequestMapper,
                               CustomerServiceClient customerServiceClient,
                               CreatureServiceClient creatureServiceClient,
                               TrainingServiceClient trainingServiceClient) {
        this.adoptionRepository = adoptionRepository;
        this.adoptionResponseMapper = adoptionResponseMapper;
        this.adoptionRequestMapper = adoptionRequestMapper;
        this.customerServiceClient = customerServiceClient;
        this.creatureServiceClient = creatureServiceClient;
        this.trainingServiceClient = trainingServiceClient;
    }

    @Override
    public List<AdoptionResponseModel> getAdoptions(Map<String, String> queryParams) {
        List<Adoption> adoptions = adoptionRepository.findAll();

        String customerId = queryParams.get("customerId");
        String creatureId = queryParams.get("creatureId");
        String profileStatus = queryParams.get("profileStatus");
        String adoptionStatus = queryParams.get("adoptionStatus");

        if (customerId != null && !customerId.isEmpty()) {
            adoptions = adoptionRepository.findByCustomerId(customerId);
        }
        if (creatureId != null && !creatureId.isEmpty()) {
            adoptions = adoptionRepository.findByCreatureId(creatureId);
        }
        if (profileStatus != null && !profileStatus.isEmpty()) {
            //adoptions = adoptionRepository.findByProfileStatus(AdoptionStatus.valueOf(profileStatus.toUpperCase()));
            adoptions = adoptionRepository.findByProfileStatus(ProfileStatus.valueOf(profileStatus.toUpperCase()));
        }
        if (adoptionStatus != null && !adoptionStatus.isEmpty()) {
            adoptions = adoptionRepository.findByAdoptionStatus(AdoptionStatus.valueOf(adoptionStatus.toUpperCase()));
        }

        List<AdoptionResponseModel> responseModels = adoptionResponseMapper.entityListToResponseModelList(adoptions);

        // Apply additional details to each response model
        for (int i = 0; i < adoptions.size(); i++) {
            populateAdditionalDetails(responseModels.get(i), adoptions.get(i));
        }

        return responseModels;
    }

    @Override
    public AdoptionResponseModel getAdoptionByAdoptionId(String adoptionId) {
        Adoption adoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId);

        if (adoption == null) {
            throw new NotFoundException("Provided adoptionId not found: " + adoptionId);
        }

        AdoptionResponseModel responseModel = adoptionResponseMapper.entityToResponseModel(adoption);
        populateAdditionalDetails(responseModel, adoption);

        return responseModel;
    }

    @Override
    public AdoptionResponseModel addAdoption(AdoptionRequestModel adoptionRequestModel) {
        CustomerResponseModel customer = customerServiceClient.getCustomerByCustomerId(adoptionRequestModel.getCustomerId());
        if (customer == null) {
            throw new NotFoundException("Customer not found with ID: " + adoptionRequestModel.getCustomerId());
        }

        CreatureResponseModel creature = creatureServiceClient.getCreatureByCreatureId(adoptionRequestModel.getCreatureId());
        if (creature == null) {
            throw new NotFoundException("Creature not found with ID: " + adoptionRequestModel.getCreatureId());
        }

        CreatureStatus creatureStatus = CreatureStatus.valueOf(creature.getStatus());
        if (creatureStatus != CreatureStatus.AVAILABLE &&
                creatureStatus != CreatureStatus.RESERVED) {
            throw new InvalidInputException("Creature is not available for adoption. Current status: " + creature.getStatus());
        }

        // Count completed adoptions for current customer
        long completedAdoptions = adoptionRepository.findByCustomerId(adoptionRequestModel.getCustomerId()).stream()
                .filter(a -> a.getAdoptionStatus() == AdoptionStatus.COMPLETED)
                .count();

        // Aggregate invariant: Check if customer has reached max adoptions limit
        if (completedAdoptions >= MAX_ADOPTIONS_PER_CUSTOMER) {
            throw new AdoptionLimitExceededException("Customer has reached the maximum limit of " + MAX_ADOPTIONS_PER_CUSTOMER + " adoptions");
        }

        Adoption adoption = adoptionRequestMapper.requestModelToEntity(adoptionRequestModel, new AdoptionIdentifier(null));

        if (adoption.getAdoptionStatus() == null) {
            adoption.setAdoptionStatus(AdoptionStatus.PENDING);
        }

        // Let the aggregate root determine the appropriate creature status
        CreatureStatus newCreatureStatus = adoption.updateAdoptionStatus(AdoptionStatus.PENDING);
        creatureServiceClient.updateCreatureStatus(adoptionRequestModel.getCreatureId(), newCreatureStatus);

        Adoption savedAdoption = adoptionRepository.save(adoption);
        AdoptionResponseModel responseModel = adoptionResponseMapper.entityToResponseModel(savedAdoption);
        populateAdditionalDetails(responseModel, savedAdoption);

        return responseModel;
    }

    @Override
    public AdoptionResponseModel updateAdoption(AdoptionRequestModel adoptionRequestModel, String adoptionId) {
        Adoption existingAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId);

        if (existingAdoption == null) {
            throw new NotFoundException("Provided adoptionId not found: " + adoptionId);
        }

        AdoptionStatus previousStatus = existingAdoption.getAdoptionStatus();

        // Keep the existing adoptionIdentifier
        Adoption updatedAdoption = adoptionRequestMapper.requestModelToEntity(adoptionRequestModel,
                existingAdoption.getAdoptionIdentifier());
        updatedAdoption.setId(existingAdoption.getId());
        updatedAdoption.setLastUpdated(LocalDateTime.now());

        if (previousStatus != updatedAdoption.getAdoptionStatus()) {
            CreatureStatus newCreatureStatus = updatedAdoption.updateAdoptionStatus(updatedAdoption.getAdoptionStatus());
            creatureServiceClient.updateCreatureStatus(updatedAdoption.getCreatureId(), newCreatureStatus);
        }

        Adoption savedAdoption = adoptionRepository.save(updatedAdoption);
        AdoptionResponseModel responseModel = adoptionResponseMapper.entityToResponseModel(savedAdoption);
        populateAdditionalDetails(responseModel, savedAdoption);

        return responseModel;
    }

    @Override
    public AdoptionResponseModel updateAdoptionStatus(String adoptionId, String newStatusStr) {
        Adoption existingAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId);

        if (existingAdoption == null) {
            throw new NotFoundException("Provided adoptionId not found: " + adoptionId);
        }

        try {
            AdoptionStatus newStatus = AdoptionStatus.valueOf(newStatusStr.toUpperCase());

            // Let the aggregate root determine the appropriate creature status
            CreatureStatus newCreatureStatus = existingAdoption.updateAdoptionStatus(newStatus);

            // Update creature based on aggregate root's decision
            creatureServiceClient.updateCreatureStatus(existingAdoption.getCreatureId(), newCreatureStatus);

            Adoption savedAdoption = adoptionRepository.save(existingAdoption);
            AdoptionResponseModel responseModel = adoptionResponseMapper.entityToResponseModel(savedAdoption);
            populateAdditionalDetails(responseModel, savedAdoption);

            return responseModel;
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid adoption status: " + newStatusStr);
        }
    }

    @Override
    public void removeAdoption(String adoptionId) {
        Adoption existingAdoption = adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId);

        if (existingAdoption == null) {
            throw new NotFoundException("Provided adoptionId not found: " + adoptionId);
        }

        // Let the aggregate root validate if deletion is allowed
        existingAdoption.validateDeletion();

        // Reset creature status to AVAILABLE
        creatureServiceClient.updateCreatureStatus(existingAdoption.getCreatureId(), CreatureStatus.AVAILABLE);

        adoptionRepository.delete(existingAdoption);
    }

    private void populateAdditionalDetails(AdoptionResponseModel response, Adoption adoption) {
        try {
            CustomerResponseModel customer = customerServiceClient.getCustomerByCustomerId(adoption.getCustomerId());
            if (customer != null) {
                response.setCustomerFirstName(customer.getFirstName());
                response.setCustomerLastName(customer.getLastName());
            }
        } catch (Exception e) {
            log.warn("Could not fetch customer details: {}", e.getMessage());
        }

        try {
            CreatureResponseModel creature = creatureServiceClient.getCreatureByCreatureId(adoption.getCreatureId());
            if (creature != null) {
                response.setCreatureName(creature.getName());
                response.setCreatureSpecies(creature.getSpecies());
                response.setCreatureStatus(CreatureStatus.valueOf(creature.getStatus()));
            }
        } catch (Exception e) {
            log.warn("Could not fetch creature details: {}", e.getMessage());
        }

        if (adoption.getTrainingId() != null) {
            try {
                TrainingResponseModel training = trainingServiceClient.getTrainingByTrainingId(adoption.getTrainingId());
                if (training != null) {
                    response.setTrainingName(training.getName());
                    response.setTrainingLocation(training.getLocation());
                }
            } catch (Exception e) {
                log.warn("Could not fetch training details: {}", e.getMessage());
            }
        } else {
            response.setTrainingName(null);
            response.setTrainingLocation(null);
        }
    }
}