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
import com.creatureadoption.adoptions.utils.HttpErrorInfo;
import com.creatureadoption.adoptions.utils.exceptions.AdoptionLimitExceededException;
import com.creatureadoption.adoptions.utils.exceptions.InvalidInputException;
import com.creatureadoption.adoptions.utils.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdoptionServiceUnitTest {

    @Mock private AdoptionRepository adoptionRepository;
    @Mock private AdoptionResponseMapper adoptionResponseMapper;
    @Mock private AdoptionRequestMapper adoptionRequestMapper;
    @Mock private CustomerServiceClient customerServiceClient;
    @Mock private CreatureServiceClient creatureServiceClient;
    @Mock private TrainingServiceClient trainingServiceClient;
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;

    @InjectMocks private AdoptionServiceImpl adoptionService;

    private AdoptionRequestModel adoptionRequestModel;
    private Adoption adoption;
    private AdoptionResponseModel adoptionResponseModel;
    private CustomerResponseModel customerResponseModel;
    private CreatureResponseModel creatureResponseModel;
    private TrainingResponseModel trainingResponseModel;
    private String adoptionId, customerId, creatureId, trainingId;
    private List<Adoption> adoptionList;
    private List<AdoptionResponseModel> adoptionResponseList;
    private AdoptionResponseModel emptyDetailsResponse;

    @BeforeEach
    void setUp() {
        adoptionId = UUID.randomUUID().toString();
        customerId = UUID.randomUUID().toString();
        creatureId = UUID.randomUUID().toString();
        trainingId = UUID.randomUUID().toString();

        adoptionRequestModel = AdoptionRequestModel.builder()
                .summary("Test Adoption").totalAdoptions(0)
                .profileCreationDate(LocalDate.now()).profileStatus(ProfileStatus.ACTIVE)
                .adoptionDate(LocalDate.now().plusDays(7)).adoptionLocation("Test Location")
                .adoptionStatus(AdoptionStatus.PENDING).specialNotes("Test Notes")
                .customerId(customerId).creatureId(creatureId).trainingId(trainingId)
                .build();

        AdoptionIdentifier identifier = new AdoptionIdentifier("TEST-CODE");

        adoption = Adoption.builder()
                .id("1").adoptionIdentifier(identifier).summary("Test Adoption").totalAdoptions(0)
                .profileCreationDate(LocalDate.now()).profileStatus(ProfileStatus.ACTIVE)
                .lastUpdated(LocalDateTime.now()).adoptionDate(LocalDate.now().plusDays(7))
                .adoptionLocation("Test Location").adoptionStatus(AdoptionStatus.PENDING)
                .specialNotes("Test Notes").customerId(customerId)
                .creatureId(creatureId).trainingId(trainingId).build();

        adoptionResponseModel = new AdoptionResponseModel();
        adoptionResponseModel.setAdoptionId(adoption.getAdoptionIdentifier().getAdoptionId());
        adoptionResponseModel.setAdoptionCode(adoption.getAdoptionIdentifier().getAdoptionCode());
        adoptionResponseModel.setSummary(adoption.getSummary());
        adoptionResponseModel.setCustomerId(customerId);
        adoptionResponseModel.setCreatureId(creatureId);
        adoptionResponseModel.setTrainingId(trainingId);
        adoptionResponseModel.setCustomerFirstName("John");
        adoptionResponseModel.setCustomerLastName("Doe");

        emptyDetailsResponse = new AdoptionResponseModel();
        emptyDetailsResponse.setAdoptionId(adoption.getAdoptionIdentifier().getAdoptionId());
        emptyDetailsResponse.setCustomerId(customerId);
        emptyDetailsResponse.setCreatureId(creatureId);
        emptyDetailsResponse.setCustomerFirstName(null);
        emptyDetailsResponse.setCustomerLastName(null);

        adoptionList = Collections.singletonList(adoption);
        adoptionResponseList = Collections.singletonList(adoptionResponseModel);

        customerResponseModel = CustomerResponseModel.builder()
                .customerId(customerId).firstName("John").lastName("Doe").emailAddress("john.doe@example.com")
                .build();

        creatureResponseModel = CreatureResponseModel.builder()
                .creatureId(creatureId).name("Pikachu").species("Electric Mouse")
                .status(CreatureStatus.AVAILABLE.toString()).build();

        trainingResponseModel = TrainingResponseModel.builder()
                .trainingId(trainingId).name("Basic Training").location("Training Center").build();
    }

    @Test
    void testGetAdoptions_NoParams() {
        // Setup
        when(adoptionRepository.findAll()).thenReturn(adoptionList);
        when(adoptionResponseMapper.entityListToResponseModelList(any())).thenReturn(adoptionResponseList);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        assertNotNull(adoptionService.getAdoptions(Collections.emptyMap()));
        verify(adoptionRepository).findAll();
    }

    @Test
    void testGetAdoptions_ByCustomerId() {
        // Setup
        when(adoptionRepository.findByCustomerId(customerId)).thenReturn(adoptionList);
        when(adoptionResponseMapper.entityListToResponseModelList(any())).thenReturn(adoptionResponseList);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        assertNotNull(adoptionService.getAdoptions(Collections.singletonMap("customerId", customerId)));
        verify(adoptionRepository).findByCustomerId(customerId);
    }

    @Test
    void testGetAdoptions_ByCreatureId() {
        // Setup
        when(adoptionRepository.findByCreatureId(creatureId)).thenReturn(adoptionList);
        when(adoptionResponseMapper.entityListToResponseModelList(any())).thenReturn(adoptionResponseList);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        assertNotNull(adoptionService.getAdoptions(Collections.singletonMap("creatureId", creatureId)));
        verify(adoptionRepository).findByCreatureId(creatureId);
    }

    @Test
    void testGetAdoptions_ByProfileStatus() {
        // Setup
        when(adoptionRepository.findByProfileStatus(any())).thenReturn(adoptionList);
        when(adoptionResponseMapper.entityListToResponseModelList(any())).thenReturn(adoptionResponseList);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        assertNotNull(adoptionService.getAdoptions(Collections.singletonMap("profileStatus", "ACTIVE")));
        verify(adoptionRepository).findByProfileStatus(ProfileStatus.ACTIVE);
    }

    @Test
    void testGetAdoptions_ByAdoptionStatus() {
        // Setup
        when(adoptionRepository.findByAdoptionStatus(any())).thenReturn(adoptionList);
        when(adoptionResponseMapper.entityListToResponseModelList(any())).thenReturn(adoptionResponseList);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        assertNotNull(adoptionService.getAdoptions(Collections.singletonMap("adoptionStatus", "PENDING")));
        verify(adoptionRepository).findByAdoptionStatus(AdoptionStatus.PENDING);
    }

    @Test
    void testGetAdoptionByAdoptionId_Success() {
        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(adoption);
        when(adoptionResponseMapper.entityToResponseModel(adoption)).thenReturn(adoptionResponseModel);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        AdoptionResponseModel result = adoptionService.getAdoptionByAdoptionId(adoptionId);

        assertNotNull(result);
        assertEquals(adoptionResponseModel.getAdoptionId(), result.getAdoptionId());
    }

    @Test
    void testGetAdoptionByAdoptionId_NotFound() {
        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionService.getAdoptionByAdoptionId(adoptionId));
    }

    @Test
    void testGetAdoptions_WithClientException() {
        when(adoptionRepository.findAll()).thenReturn(adoptionList);
        when(adoptionResponseMapper.entityListToResponseModelList(any())).thenReturn(Collections.singletonList(emptyDetailsResponse));
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenThrow(new RuntimeException("Service unreachable"));
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        List<AdoptionResponseModel> result = adoptionService.getAdoptions(Collections.emptyMap());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getCustomerFirstName());
        assertNull(result.get(0).getCustomerLastName());
    }

    @Test
    void testAddAdoption_Success() {
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(adoptionRequestMapper.requestModelToEntity(eq(adoptionRequestModel), any(AdoptionIdentifier.class))).thenReturn(adoption);
        when(adoptionRepository.findByCustomerId(customerId)).thenReturn(Collections.emptyList());
        when(adoptionRepository.save(any(Adoption.class))).thenReturn(adoption);
        when(adoptionResponseMapper.entityToResponseModel(adoption)).thenReturn(adoptionResponseModel);
        when(creatureServiceClient.updateCreatureStatus(eq(creatureId), any(CreatureStatus.class))).thenReturn(creatureResponseModel);

        AdoptionResponseModel result = adoptionService.addAdoption(adoptionRequestModel);

        assertNotNull(result);
        assertEquals(adoptionResponseModel.getAdoptionId(), result.getAdoptionId());
    }

    @Test
    void testAddAdoption_CustomerNotFound() {
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionService.addAdoption(adoptionRequestModel));
    }

    @Test
    void testAddAdoption_CreatureNotFound() {
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionService.addAdoption(adoptionRequestModel));
    }

    @Test
    void testAddAdoption_CreatureNotAvailable() {
        CreatureResponseModel unavailableCreature = CreatureResponseModel.builder()
                .creatureId(creatureId).status(CreatureStatus.ADOPTED.toString()).build();
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(unavailableCreature);

        assertThrows(InvalidInputException.class, () ->
                adoptionService.addAdoption(adoptionRequestModel));
    }

    @Test
    void testAddAdoption_LimitExceeded() {
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);

        List<Adoption> completedAdoptions = Arrays.asList(
                Adoption.builder().adoptionStatus(AdoptionStatus.COMPLETED).build(),
                Adoption.builder().adoptionStatus(AdoptionStatus.COMPLETED).build()
        );
        when(adoptionRepository.findByCustomerId(customerId)).thenReturn(completedAdoptions);

        assertThrows(AdoptionLimitExceededException.class, () ->
                adoptionService.addAdoption(adoptionRequestModel));
    }

    @Test
    void testUpdateAdoption_Success() {
        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(adoption);
        when(adoptionRequestMapper.requestModelToEntity(eq(adoptionRequestModel), any(AdoptionIdentifier.class))).thenReturn(adoption);
        when(adoptionRepository.save(any(Adoption.class))).thenReturn(adoption);
        when(adoptionResponseMapper.entityToResponseModel(adoption)).thenReturn(adoptionResponseModel);
        when(customerServiceClient.getCustomerByCustomerId(customerId)).thenReturn(customerResponseModel);
        when(creatureServiceClient.getCreatureByCreatureId(creatureId)).thenReturn(creatureResponseModel);
        when(trainingServiceClient.getTrainingByTrainingId(trainingId)).thenReturn(trainingResponseModel);

        AdoptionResponseModel result = adoptionService.updateAdoption(adoptionRequestModel, adoptionId);

        assertNotNull(result);
        assertEquals(adoptionResponseModel.getAdoptionId(), result.getAdoptionId());
    }

    @Test
    void testUpdateAdoption_NotFound() {
        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionService.updateAdoption(adoptionRequestModel, adoptionId));
    }

    @Test
    void testUpdateAdoptionStatus_AllValidStatuses() {
        testSingleStatus("PENDING", CreatureStatus.ADOPTION_PENDING);
        testSingleStatus("APPROVED", CreatureStatus.RESERVED);
        testSingleStatus("COMPLETED", CreatureStatus.ADOPTED);
        testSingleStatus("CANCELLED", CreatureStatus.AVAILABLE);
        testSingleStatus("RETURNED", CreatureStatus.AVAILABLE);
    }

    private void testSingleStatus(String status, CreatureStatus expectedCreatureStatus) {
        // Create fresh mocks for this test
        AdoptionRepository mockRepo = mock(AdoptionRepository.class);
        AdoptionResponseMapper mockMapper = mock(AdoptionResponseMapper.class);
        CreatureServiceClient mockCreatureClient = mock(CreatureServiceClient.class);

        // Create a service instance with these mocks
        AdoptionServiceImpl service = new AdoptionServiceImpl(
                mockRepo,
                mockMapper,
                mock(AdoptionRequestMapper.class),
                mock(CustomerServiceClient.class),
                mockCreatureClient,
                mock(TrainingServiceClient.class)
        );

        // Set up mocks
        Adoption testAdoption = Adoption.builder()
                .id("1").adoptionIdentifier(new AdoptionIdentifier(adoptionId, "TEST-CODE"))
                .adoptionStatus(AdoptionStatus.PENDING).creatureId(creatureId).build();

        when(mockRepo.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(testAdoption);
        when(mockRepo.save(any())).thenReturn(testAdoption);
        when(mockMapper.entityToResponseModel(any())).thenReturn(adoptionResponseModel);
        when(mockCreatureClient.updateCreatureStatus(eq(creatureId), eq(expectedCreatureStatus)))
                .thenReturn(creatureResponseModel);

        // Act
        service.updateAdoptionStatus(adoptionId, status);

        // Verify
        verify(mockCreatureClient).updateCreatureStatus(eq(creatureId), eq(expectedCreatureStatus));
    }

    @Test
    void testUpdateAdoptionStatus_InvalidStatus() {
        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(adoption);

        assertThrows(InvalidInputException.class, () ->
                adoptionService.updateAdoptionStatus(adoptionId, "INVALID_STATUS"));
    }

    @Test
    void testRemoveAdoption_Success() {
        Adoption pendingAdoption = Adoption.builder()
                .id("1").adoptionIdentifier(new AdoptionIdentifier(adoptionId, "TEST-CODE"))
                .summary("Test Adoption").adoptionStatus(AdoptionStatus.PENDING).creatureId(creatureId).build();

        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(pendingAdoption);
        when(creatureServiceClient.updateCreatureStatus(eq(creatureId), eq(CreatureStatus.AVAILABLE))).thenReturn(creatureResponseModel);

        adoptionService.removeAdoption(adoptionId);

        verify(adoptionRepository).delete(pendingAdoption);
    }

    @Test
    void testRemoveAdoption_NotFound() {
        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(null);

        assertThrows(NotFoundException.class, () ->
                adoptionService.removeAdoption(adoptionId));
    }

    @Test
    void testRemoveAdoption_CompletedAdoption() {
        Adoption completedAdoption = Adoption.builder()
                .id("1").adoptionIdentifier(new AdoptionIdentifier(adoptionId, "TEST-CODE"))
                .summary("Test Adoption").adoptionStatus(AdoptionStatus.COMPLETED).creatureId(creatureId).build();

        when(adoptionRepository.findByAdoptionIdentifier_AdoptionId(adoptionId)).thenReturn(completedAdoption);

        assertThrows(InvalidInputException.class, () ->
                adoptionService.removeAdoption(adoptionId));
    }

    @Test
    void testAdoptionDomainMethods() {
        // Test AdoptionIdentifier constructors
        AdoptionIdentifier id1 = new AdoptionIdentifier();
        AdoptionIdentifier id2 = new AdoptionIdentifier("TEST-CODE");
        AdoptionIdentifier id3 = new AdoptionIdentifier(null);
        AdoptionIdentifier id4 = new AdoptionIdentifier("uuid", "TEST-CODE");

        assertNull(id1.getAdoptionId());
        assertNotNull(id2.getAdoptionId());
        assertNotNull(id3.getAdoptionId());
        assertTrue(id3.getAdoptionCode().startsWith("ADO-"));
        assertEquals("uuid", id4.getAdoptionId());

        // Test Adoption domain status transitions
        Adoption testAdoption = Adoption.builder().adoptionStatus(AdoptionStatus.PENDING).build();

        assertEquals(CreatureStatus.ADOPTION_PENDING, testAdoption.updateAdoptionStatus(AdoptionStatus.PENDING));
        assertEquals(CreatureStatus.RESERVED, testAdoption.updateAdoptionStatus(AdoptionStatus.APPROVED));
        assertEquals(CreatureStatus.ADOPTED, testAdoption.updateAdoptionStatus(AdoptionStatus.COMPLETED));
        assertEquals(CreatureStatus.AVAILABLE, testAdoption.updateAdoptionStatus(AdoptionStatus.CANCELLED));
        assertEquals(CreatureStatus.AVAILABLE, testAdoption.updateAdoptionStatus(AdoptionStatus.RETURNED));

        // Test validateDeletion with non-completed status
        testAdoption.setAdoptionStatus(AdoptionStatus.PENDING);
        assertDoesNotThrow(() -> testAdoption.validateDeletion());

        // Test validateDeletion with completed status
        testAdoption.setAdoptionStatus(AdoptionStatus.COMPLETED);
        assertThrows(InvalidInputException.class, () -> testAdoption.validateDeletion());

        // Test validateCustomerAdoptionLimit
        assertDoesNotThrow(() -> Adoption.validateCustomerAdoptionLimit(1, 2));
        assertThrows(InvalidInputException.class, () -> Adoption.validateCustomerAdoptionLimit(2, 2));
        assertThrows(InvalidInputException.class, () -> Adoption.validateCustomerAdoptionLimit(3, 2));
    }

    @Test
    void testServiceClients() throws IOException {
        // Common mocks for clients
        RestTemplate mockRest = mock(RestTemplate.class);
        ObjectMapper mockMapper = mock(ObjectMapper.class);

        // Test CustomerServiceClient
        CustomerServiceClient customerClient = new CustomerServiceClient(mockRest, mockMapper, "host", "port");
        String customerUrl = "http://host:port/api/v1/customers/" + customerId;

        when(mockRest.getForObject(eq(customerUrl), eq(CustomerResponseModel.class)))
                .thenReturn(customerResponseModel);
        assertNotNull(customerClient.getCustomerByCustomerId(customerId));

        // Test CustomerServiceClient error handling - NOT_FOUND
        HttpClientErrorException notFoundException = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null,
                "{'message':'Customer not found'}".getBytes(), StandardCharsets.UTF_8);

        when(mockRest.getForObject(eq(customerUrl), eq(CustomerResponseModel.class)))
                .thenThrow(notFoundException);

        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/path", "Customer not found");
        when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(errorInfo);

        assertThrows(NotFoundException.class, () -> customerClient.getCustomerByCustomerId(customerId));

        // Test CustomerServiceClient error handling - UNPROCESSABLE_ENTITY
        HttpClientErrorException unprocessableException = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable", null,
                "{'message':'Invalid input'}".getBytes(), StandardCharsets.UTF_8);

        when(mockRest.getForObject(eq(customerUrl), eq(CustomerResponseModel.class)))
                .thenThrow(unprocessableException);

        HttpErrorInfo unprocessableError = new HttpErrorInfo(
                HttpStatus.UNPROCESSABLE_ENTITY, "/path", "Invalid input");
        when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
                .thenReturn(unprocessableError);

        assertThrows(InvalidInputException.class, () -> customerClient.getCustomerByCustomerId(customerId));

        // Test CreatureServiceClient
        CreatureServiceClient creatureClient = new CreatureServiceClient(mockRest, mockMapper, "host", "port");
        String creatureUrl = "http://host:port/api/v1/creatures/" + creatureId;

        when(mockRest.getForObject(eq(creatureUrl), eq(CreatureResponseModel.class)))
                .thenReturn(creatureResponseModel);
        assertNotNull(creatureClient.getCreatureByCreatureId(creatureId));

        // Test updateCreatureStatus
        ResponseEntity<CreatureResponseModel> responseEntity =
                new ResponseEntity<>(creatureResponseModel, HttpStatus.OK);

        when(mockRest.exchange(eq(creatureUrl), eq(HttpMethod.PUT),
                any(HttpEntity.class), eq(CreatureResponseModel.class)))
                .thenReturn(responseEntity);

        assertNotNull(creatureClient.updateCreatureStatus(creatureId, CreatureStatus.ADOPTED));

        // Test TrainingServiceClient
        TrainingServiceClient trainingClient = new TrainingServiceClient(mockRest, mockMapper, "host", "port");
        String trainingUrl = "http://host:port/api/v1/trainings/" + trainingId;

        when(mockRest.getForObject(eq(trainingUrl), eq(TrainingResponseModel.class)))
                .thenReturn(trainingResponseModel);
        assertNotNull(trainingClient.getTrainingByTrainingId(trainingId));
    }

    @Test
    void testCreatureServiceClientErrorHandling() {
        // Setup mocks
        RestTemplate mockRest = mock(RestTemplate.class);
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        CreatureServiceClient client = new CreatureServiceClient(mockRest, mockMapper, "host", "port");
        String creatureUrl = "http://host:port/api/v1/creatures/" + creatureId;

        // Test NOT_FOUND exception
        HttpClientErrorException notFoundException = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null,
                "{'message':'Creature not found'}".getBytes(), StandardCharsets.UTF_8);
        when(mockRest.getForObject(eq(creatureUrl), eq(CreatureResponseModel.class)))
                .thenThrow(notFoundException);
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/path", "Creature not found");
        try {
            when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);
        } catch (IOException e) {
            fail("Should not throw exception during test setup");
        }
        assertThrows(NotFoundException.class, () -> client.getCreatureByCreatureId(creatureId));

        // Test UNPROCESSABLE_ENTITY exception
        HttpClientErrorException unprocessableException = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable", null,
                "{'message':'Invalid creature data'}".getBytes(), StandardCharsets.UTF_8);
        when(mockRest.getForObject(eq(creatureUrl), eq(CreatureResponseModel.class)))
                .thenThrow(unprocessableException);
        HttpErrorInfo unprocessableError = new HttpErrorInfo(
                HttpStatus.UNPROCESSABLE_ENTITY, "/path", "Invalid creature data");
        try {
            when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(unprocessableError);
        } catch (IOException e) {
            fail("Should not throw exception during test setup");
        }
        assertThrows(InvalidInputException.class, () -> client.getCreatureByCreatureId(creatureId));

        // Test unexpected HTTP exception
        HttpClientErrorException unexpectedException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", null,
                "{'message':'Bad request'}".getBytes(), StandardCharsets.UTF_8);
        when(mockRest.getForObject(eq(creatureUrl), eq(CreatureResponseModel.class)))
                .thenThrow(unexpectedException);
        assertThrows(HttpClientErrorException.class, () -> client.getCreatureByCreatureId(creatureId));
    }

    @Test
    void testTrainingServiceClientErrorHandling() throws IOException {
        // Setup mocks
        RestTemplate mockRest = mock(RestTemplate.class);
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        TrainingServiceClient client = new TrainingServiceClient(mockRest, mockMapper, "host", "port");
        String trainingUrl = "http://host:port/api/v1/trainings/" + trainingId;

        // Test NOT_FOUND exception
        HttpClientErrorException notFoundException = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null,
                "{'message':'Training not found'}".getBytes(), StandardCharsets.UTF_8);
        when(mockRest.getForObject(eq(trainingUrl), eq(TrainingResponseModel.class)))
                .thenThrow(notFoundException);
        HttpErrorInfo errorInfo = new HttpErrorInfo(HttpStatus.NOT_FOUND, "/path", "Training not found");
        when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(errorInfo);
        assertThrows(NotFoundException.class, () -> client.getTrainingByTrainingId(trainingId));

        // Test UNPROCESSABLE_ENTITY exception
        HttpClientErrorException unprocessableException = HttpClientErrorException.create(
                HttpStatus.UNPROCESSABLE_ENTITY, "Unprocessable", null,
                "{'message':'Invalid training data'}".getBytes(), StandardCharsets.UTF_8);
        when(mockRest.getForObject(eq(trainingUrl), eq(TrainingResponseModel.class)))
                .thenThrow(unprocessableException);
        HttpErrorInfo unprocessableError = new HttpErrorInfo(
                HttpStatus.UNPROCESSABLE_ENTITY, "/path", "Invalid training data");
        when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class))).thenReturn(unprocessableError);
        assertThrows(InvalidInputException.class, () -> client.getTrainingByTrainingId(trainingId));

        // Test unexpected HTTP exception
        HttpClientErrorException unexpectedException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", null,
                "{'message':'Bad request'}".getBytes(), StandardCharsets.UTF_8);
        when(mockRest.getForObject(eq(trainingUrl), eq(TrainingResponseModel.class)))
                .thenThrow(unexpectedException);
        assertThrows(HttpClientErrorException.class, () -> client.getTrainingByTrainingId(trainingId));
    }

    @Test
    void testClientErrorHandlingUnexpectedException() {
        // Setup mocks
        RestTemplate mockRest = mock(RestTemplate.class);
        ObjectMapper mockMapper = mock(ObjectMapper.class);
        CustomerServiceClient client = new CustomerServiceClient(mockRest, mockMapper, "host", "port");
        String url = "http://host:port/api/v1/customers/" + customerId;

        // Test unexpected HTTP exception
        HttpClientErrorException unexpectedException = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST, "Bad Request", null,
                "{'message':'Bad request'}".getBytes(), StandardCharsets.UTF_8);

        when(mockRest.getForObject(eq(url), eq(CustomerResponseModel.class)))
                .thenThrow(unexpectedException);

        assertThrows(HttpClientErrorException.class, () -> client.getCustomerByCustomerId(customerId));
    }

//    @Test
//    void testClientErrorHandlingErrorParsing() throws JsonProcessingException {
//        // Setup mocks
//        RestTemplate mockRest = mock(RestTemplate.class);
//        ObjectMapper mockMapper = mock(ObjectMapper.class);
//        CustomerServiceClient client = new CustomerServiceClient(mockRest, mockMapper, "host", "port");
//        String url = "http://host:port/api/v1/customers/" + customerId;
//
//        // Test error when parsing the error body
//        HttpClientErrorException exception = HttpClientErrorException.create(
//                HttpStatus.NOT_FOUND, "Not Found", null,
//                "{'message':'Not found'}".getBytes(), StandardCharsets.UTF_8);
//
//        when(mockRest.getForObject(eq(url), eq(CustomerResponseModel.class)))
//                .thenThrow(exception);
//
//        // Instead of throwing a checked exception, just return null to simulate error parsing
//        when(mockMapper.readValue(anyString(), eq(HttpErrorInfo.class)))
//                .thenReturn(null);
//
//        assertThrows(NotFoundException.class, () -> client.getCustomerByCustomerId(customerId));
//    }
}