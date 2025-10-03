package com.creatureadoption.apigateway.businesslayer.customers;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionStatus;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.adoptions.ProfileStatus;
import com.creatureadoption.apigateway.domainclientlayer.customers.ContactMethodPreference;
import com.creatureadoption.apigateway.domainclientlayer.customers.CustomersServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.customers.PhoneNumber;
import com.creatureadoption.apigateway.domainclientlayer.customers.PhoneType;
import com.creatureadoption.apigateway.domainclientlayer.creatures.CreatureStatus;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerResponseModel;
import com.creatureadoption.apigateway.utils.exceptions.EntityInUseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomersServiceImplTest {

    @Mock
    private CustomersServiceClient customersServiceClient;

    @Mock
    private AdoptionsServiceClient adoptionsServiceClient;

    @InjectMocks
    private CustomersServiceImpl customersService;

    private final String CUSTOMER_ID = "f2f7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private CustomerResponseModel customerResponseModel;
    private CustomerRequestModel customerRequestModel;
    private AdoptionResponseModel adoptionResponseModel;
    private List<CustomerResponseModel> customerResponseModels;
    private Map<String, String> queryParams;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        PhoneNumber phoneNumber = new PhoneNumber(PhoneType.MOBILE, "123-456-7890");
        List<PhoneNumber> phoneNumbers = Collections.singletonList(phoneNumber);

        customerResponseModel = new CustomerResponseModel(
                CUSTOMER_ID,
                "John",
                "Doe",
                "john.doe@example.com",
                ContactMethodPreference.EMAIL,
                "123 Main St",
                "City",
                "Province",
                "Country",
                "12345",
                phoneNumbers
        );

        customerRequestModel = CustomerRequestModel.builder()
                .firstName("John")
                .lastName("Doe")
                .emailAddress("john.doe@example.com")
                .contactMethodPreference(ContactMethodPreference.EMAIL)
                .streetAddress("123 Main St")
                .city("City")
                .province("Province")
                .country("Country")
                .postalCode("12345")
                .phoneNumbers(phoneNumbers)
                .build();

        adoptionResponseModel = new AdoptionResponseModel(
                "a2a7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "ADO-12345",
                "Customer adoption",
                1,
                LocalDate.now(),
                LocalDateTime.now(),
                ProfileStatus.ACTIVE,
                LocalDate.now(),
                "Creature Adoption Center",
                AdoptionStatus.APPROVED,
                "Notes",
                CUSTOMER_ID,  // Use the test class's customer ID
                "John",
                "Doe",
                "c2c7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "Sparky",
                "Dragon",
                CreatureStatus.ADOPTED,
                "t2t7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a",
                "Fire Training",
                "Academy"
        );

        customerResponseModels = Collections.singletonList(customerResponseModel);
        queryParams = new HashMap<>();
    }

    @Test
    void getCustomers_ShouldCallClientAndReturnCustomers() {
        when(customersServiceClient.getCustomers(queryParams)).thenReturn(customerResponseModels);

        List<CustomerResponseModel> result = customersService.getCustomers(queryParams);

        assertEquals(customerResponseModels, result);
        verify(customersServiceClient, times(1)).getCustomers(queryParams);
    }

    @Test
    void getCustomerByCustomerId_ShouldCallClientAndReturnCustomer() {
        when(customersServiceClient.getCustomerByCustomerId(CUSTOMER_ID)).thenReturn(customerResponseModel);

        CustomerResponseModel result = customersService.getCustomerByCustomerId(CUSTOMER_ID);

        assertEquals(customerResponseModel, result);
        verify(customersServiceClient, times(1)).getCustomerByCustomerId(CUSTOMER_ID);
    }

    @Test
    void addCustomer_ShouldCallClientAndReturnCreatedCustomer() {
        when(customersServiceClient.addCustomer(customerRequestModel)).thenReturn(customerResponseModel);

        CustomerResponseModel result = customersService.addCustomer(customerRequestModel);

        assertEquals(customerResponseModel, result);
        verify(customersServiceClient, times(1)).addCustomer(customerRequestModel);
    }

    @Test
    void updateCustomer_ShouldCallClientAndReturnUpdatedCustomer() {
        when(customersServiceClient.updateCustomer(customerRequestModel, CUSTOMER_ID)).thenReturn(customerResponseModel);

        CustomerResponseModel result = customersService.updateCustomer(customerRequestModel, CUSTOMER_ID);

        assertEquals(customerResponseModel, result);
        verify(customersServiceClient, times(1)).updateCustomer(customerRequestModel, CUSTOMER_ID);
    }

    @Test
    void removeCustomer_ShouldCallClient() {
        doNothing().when(customersServiceClient).removeCustomer(CUSTOMER_ID);

        customersService.removeCustomer(CUSTOMER_ID);

        verify(customersServiceClient, times(1)).removeCustomer(CUSTOMER_ID);
    }

    @Test
    void removeCustomer_ShouldThrowEntityInUseException_WhenAdoptionsExist() {
        // Setup
        Map<String, String> expectedQueryParams = Collections.singletonMap("customerId", CUSTOMER_ID);
        when(adoptionsServiceClient.getAdoptions(expectedQueryParams))
                .thenReturn(Collections.singletonList(adoptionResponseModel));

        // Execute and verify
        EntityInUseException exception = assertThrows(EntityInUseException.class, () ->
                customersService.removeCustomer(CUSTOMER_ID));

        assertEquals("Cannot delete customer with ID: " + CUSTOMER_ID +
                " because they have existing adoptions", exception.getMessage());
        verify(adoptionsServiceClient).getAdoptions(expectedQueryParams);
        verify(customersServiceClient, never()).removeCustomer(CUSTOMER_ID);
    }

    @Test
    void removeCustomer_ShouldRemoveCustomer_WhenNoAdoptionsExist() {
        // Setup
        Map<String, String> expectedQueryParams = Collections.singletonMap("customerId", CUSTOMER_ID);
        when(adoptionsServiceClient.getAdoptions(expectedQueryParams))
                .thenReturn(Collections.emptyList());

        // Execute
        customersService.removeCustomer(CUSTOMER_ID);

        // Verify
        verify(adoptionsServiceClient).getAdoptions(expectedQueryParams);
        verify(customersServiceClient).removeCustomer(CUSTOMER_ID);
    }
}