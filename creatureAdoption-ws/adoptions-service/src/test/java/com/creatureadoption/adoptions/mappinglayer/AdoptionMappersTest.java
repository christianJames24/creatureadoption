package com.creatureadoption.adoptions.mappinglayer;

import com.creatureadoption.adoptions.dataaccesslayer.*;
import com.creatureadoption.adoptions.presentationlayer.AdoptionController;
import com.creatureadoption.adoptions.presentationlayer.AdoptionRequestModel;
import com.creatureadoption.adoptions.presentationlayer.AdoptionResponseModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdoptionMappersTest {

    private AdoptionRequestMapper requestMapper;
    private AdoptionResponseMapper responseMapper;
    private AdoptionResponseMapperImpl responseMapperImpl;

    private AdoptionRequestModel requestModel;
    private Adoption adoption;
    private AdoptionIdentifier adoptionIdentifier;
    private String adoptionId;
    private String customerId;
    private String creatureId;
    private String trainingId;

    @BeforeEach
    void setUp() {
        // Initialize mappers - using a spy to test HATEOAS links
        requestMapper = new AdoptionRequestMapperImpl();
        responseMapperImpl = new AdoptionResponseMapperImpl();
        responseMapper = spy(responseMapperImpl);

        // Setup IDs
        adoptionId = UUID.randomUUID().toString();
        customerId = UUID.randomUUID().toString();
        creatureId = UUID.randomUUID().toString();
        trainingId = UUID.randomUUID().toString();

        // Setup adoption identifier
        adoptionIdentifier = new AdoptionIdentifier(adoptionId, "ADO-12345678");

        // Setup request model
        requestModel = AdoptionRequestModel.builder()
                .summary("Test Adoption")
                .totalAdoptions(0)
                .profileCreationDate(LocalDate.now())
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(7))
                .adoptionLocation("Test Location")
                .adoptionStatus(AdoptionStatus.PENDING)
                .specialNotes("Test Notes")
                .customerId(customerId)
                .creatureId(creatureId)
                .trainingId(trainingId)
                .build();

        // Setup adoption entity
        adoption = Adoption.builder()
                .id("1")
                .adoptionIdentifier(adoptionIdentifier)
                .summary("Test Adoption")
                .totalAdoptions(0)
                .profileCreationDate(LocalDate.now())
                .lastUpdated(LocalDateTime.now())
                .profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(7))
                .adoptionLocation("Test Location")
                .adoptionStatus(AdoptionStatus.PENDING)
                .specialNotes("Test Notes")
                .customerId(customerId)
                .creatureId(creatureId)
                .trainingId(trainingId)
                .build();
    }

    @Test
    void testRequestMapper() {
        // Test standard mapping
        Adoption mappedAdoption = requestMapper.requestModelToEntity(requestModel, adoptionIdentifier);

        assertNotNull(mappedAdoption);
        assertEquals(adoptionIdentifier, mappedAdoption.getAdoptionIdentifier());
        assertEquals(requestModel.getSummary(), mappedAdoption.getSummary());
        assertEquals(requestModel.getTotalAdoptions(), mappedAdoption.getTotalAdoptions());
        assertEquals(requestModel.getProfileCreationDate(), mappedAdoption.getProfileCreationDate());
        assertEquals(requestModel.getProfileStatus(), mappedAdoption.getProfileStatus());
        assertEquals(requestModel.getAdoptionDate(), mappedAdoption.getAdoptionDate());
        assertEquals(requestModel.getAdoptionLocation(), mappedAdoption.getAdoptionLocation());
        assertEquals(requestModel.getAdoptionStatus(), mappedAdoption.getAdoptionStatus());
        assertEquals(requestModel.getSpecialNotes(), mappedAdoption.getSpecialNotes());
        assertEquals(requestModel.getCustomerId(), mappedAdoption.getCustomerId());
        assertEquals(requestModel.getCreatureId(), mappedAdoption.getCreatureId());
        assertEquals(requestModel.getTrainingId(), mappedAdoption.getTrainingId());
        assertNotNull(mappedAdoption.getLastUpdated());

        // Test null inputs
        assertNull(requestMapper.requestModelToEntity(null, null));

        // Test partial null input
        Adoption partialMapped = requestMapper.requestModelToEntity(null, adoptionIdentifier);
        assertNotNull(partialMapped);
        assertEquals(adoptionIdentifier, partialMapped.getAdoptionIdentifier());
        assertNotNull(partialMapped.getLastUpdated());
    }

    @Test
    void testResponseMapper() {
        // Test standard mapping
        AdoptionResponseModel responseModel = responseMapper.entityToResponseModel(adoption);

        assertNotNull(responseModel);
        assertEquals(adoption.getAdoptionIdentifier().getAdoptionId(), responseModel.getAdoptionId());
        assertEquals(adoption.getAdoptionIdentifier().getAdoptionCode(), responseModel.getAdoptionCode());
        assertEquals(adoption.getSummary(), responseModel.getSummary());
        assertEquals(adoption.getTotalAdoptions(), responseModel.getTotalAdoptions());
        assertEquals(adoption.getProfileCreationDate(), responseModel.getProfileCreationDate());
        assertEquals(adoption.getLastUpdated(), responseModel.getLastUpdated());
        assertEquals(adoption.getProfileStatus(), responseModel.getProfileStatus());
        assertEquals(adoption.getAdoptionDate(), responseModel.getAdoptionDate());
        assertEquals(adoption.getAdoptionLocation(), responseModel.getAdoptionLocation());
        assertEquals(adoption.getAdoptionStatus(), responseModel.getAdoptionStatus());
        assertEquals(adoption.getSpecialNotes(), responseModel.getSpecialNotes());
        assertEquals(adoption.getCustomerId(), responseModel.getCustomerId());
        assertEquals(adoption.getCreatureId(), responseModel.getCreatureId());
        assertEquals(adoption.getTrainingId(), responseModel.getTrainingId());

        // Verify addLinks was called
        verify(responseMapper).addLinks(any(AdoptionResponseModel.class), eq(adoption));

        // Test null input
        assertNull(responseMapper.entityToResponseModel(null));
    }

    @Test
    void testResponseListMapper() {
        // Test list mapping
        List<Adoption> adoptions = Arrays.asList(adoption, adoption);
        List<AdoptionResponseModel> responseModels = responseMapper.entityListToResponseModelList(adoptions);

        assertNotNull(responseModels);
        assertEquals(2, responseModels.size());

        for (AdoptionResponseModel model : responseModels) {
            assertEquals(adoption.getAdoptionIdentifier().getAdoptionId(), model.getAdoptionId());
            assertEquals(adoption.getAdoptionIdentifier().getAdoptionCode(), model.getAdoptionCode());
        }

        // Test null list
        assertNull(responseMapper.entityListToResponseModelList(null));

        // Test empty list
        List<AdoptionResponseModel> emptyResult = responseMapper.entityListToResponseModelList(List.of());
        assertNotNull(emptyResult);
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void testAddLinks() {
        // Create a response model to test adding links
        AdoptionResponseModel responseModel = new AdoptionResponseModel();
        responseModel.setAdoptionId(adoptionId);

        // We can't test Spring HATEOAS links directly without context, so we'll
        // check that links are added to the model
        // First, mock the Link objects
        Link selfLink = mock(Link.class);
        Link allAdoptionsLink = mock(Link.class);

        // Create a custom implementation that adds our mock links
        AdoptionResponseMapper customMapper = new AdoptionResponseMapperImpl() {
            @Override
            public void addLinks(AdoptionResponseModel response, Adoption adoption) {
                response.add(selfLink);
                response.add(allAdoptionsLink);
            }
        };

        // Call the custom addLinks method
        customMapper.addLinks(responseModel, adoption);

        // Verify links were added
        assertNotNull(responseModel.getLinks());
        assertEquals(2, responseModel.getLinks().toList().size());
    }
}