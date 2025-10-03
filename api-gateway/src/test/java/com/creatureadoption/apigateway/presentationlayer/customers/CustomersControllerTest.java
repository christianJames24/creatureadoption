package com.creatureadoption.apigateway.presentationlayer.customers;

import com.creatureadoption.apigateway.businesslayer.customers.CustomersService;
import com.creatureadoption.apigateway.domainclientlayer.customers.ContactMethodPreference;
import com.creatureadoption.apigateway.domainclientlayer.customers.PhoneNumber;
import com.creatureadoption.apigateway.domainclientlayer.customers.PhoneType;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import com.creatureadoption.apigateway.utils.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomersController.class)
class CustomersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomersService customersService;

    private final String CUSTOMER_ID = "f2f7b5a0-8f9a-4b9c-8b9a-8f9a4b9c8b9a";
    private final String INVALID_CUSTOMER_ID = "invalid-id";
    private CustomerResponseModel customerResponseModel;
    private CustomerRequestModel customerRequestModel;
    private List<CustomerResponseModel> customerResponseModels;

    @BeforeEach
    void setUp() {
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

        customerResponseModels = Collections.singletonList(customerResponseModel);
    }

//    @Test
//    void getCustomers_ShouldReturnAllCustomers() throws Exception {
//        when(customersService.getCustomers(any(Map.class))).thenReturn(customerResponseModels);
//
//        mockMvc.perform(get("/api/v1/customers")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(1)))
//                .andExpect(jsonPath("$[0].customerId", is(CUSTOMER_ID)))
//                .andExpect(jsonPath("$[0].firstName", is("John")))
//                .andExpect(jsonPath("$[0].lastName", is("Doe")))
//                .andExpect(jsonPath("$[0]._links.self.href").exists())
//                .andExpect(jsonPath("$[0]._links.allCustomers.href").exists());
//
//        verify(customersService, times(1)).getCustomers(any(Map.class));
//    }

    @Test
    void getCustomers_WithValidCustomerId_ShouldReturnFilteredCustomers() throws Exception {
        Map<String, String> queryParams = Collections.singletonMap("customerId", CUSTOMER_ID);
        when(customersService.getCustomers(eq(queryParams))).thenReturn(customerResponseModels);

        mockMvc.perform(get("/api/v1/customers")
                        .param("customerId", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId", is(CUSTOMER_ID)));

        verify(customersService, times(1)).getCustomers(eq(queryParams));
    }

    @Test
    void getCustomers_WithInvalidCustomerId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/customers")
                        .param("customerId", INVALID_CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(customersService, never()).getCustomers(any(Map.class));
    }

    @Test
    void getCustomerByCustomerId_WithValidId_ShouldReturnCustomer() throws Exception {
        when(customersService.getCustomerByCustomerId(CUSTOMER_ID)).thenReturn(customerResponseModel);

        mockMvc.perform(get("/api/v1/customers/{customerId}", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allCustomers.href").exists());

        verify(customersService, times(1)).getCustomerByCustomerId(CUSTOMER_ID);
    }

    @Test
    void getCustomerByCustomerId_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(get("/api/v1/customers/{customerId}", INVALID_CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableEntity());

        verify(customersService, never()).getCustomerByCustomerId(anyString());
    }

    @Test
    void getCustomerByCustomerId_CustomerNotFound_ShouldThrowNotFoundException() throws Exception {
        when(customersService.getCustomerByCustomerId(CUSTOMER_ID)).thenThrow(new NotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/{customerId}", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(customersService, times(1)).getCustomerByCustomerId(CUSTOMER_ID);
    }

    @Test
    void addCustomer_ShouldCreateCustomer() throws Exception {
        when(customersService.addCustomer(any(CustomerRequestModel.class))).thenReturn(customerResponseModel);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"emailAddress\":\"john.doe@example.com\",\"contactMethodPreference\":\"EMAIL\",\"streetAddress\":\"123 Main St\",\"city\":\"City\",\"province\":\"Province\",\"country\":\"Country\",\"postalCode\":\"12345\",\"phoneNumbers\":[{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allCustomers.href").exists());

        verify(customersService, times(1)).addCustomer(any(CustomerRequestModel.class));
    }

    @Test
    void updateCustomer_WithValidId_ShouldUpdateCustomer() throws Exception {
        when(customersService.updateCustomer(any(CustomerRequestModel.class), eq(CUSTOMER_ID))).thenReturn(customerResponseModel);

        mockMvc.perform(put("/api/v1/customers/{customerId}", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"emailAddress\":\"john.doe@example.com\",\"contactMethodPreference\":\"EMAIL\",\"streetAddress\":\"123 Main St\",\"city\":\"City\",\"province\":\"Province\",\"country\":\"Country\",\"postalCode\":\"12345\",\"phoneNumbers\":[{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.allCustomers.href").exists());

        verify(customersService, times(1)).updateCustomer(any(CustomerRequestModel.class), eq(CUSTOMER_ID));
    }

    @Test
    void updateCustomer_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(put("/api/v1/customers/{customerId}", INVALID_CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"emailAddress\":\"john.doe@example.com\",\"contactMethodPreference\":\"EMAIL\",\"streetAddress\":\"123 Main St\",\"city\":\"City\",\"province\":\"Province\",\"country\":\"Country\",\"postalCode\":\"12345\",\"phoneNumbers\":[{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}]}"))
                .andExpect(status().isUnprocessableEntity());

        verify(customersService, never()).updateCustomer(any(CustomerRequestModel.class), anyString());
    }

    @Test
    void updateCustomer_CustomerNotFound_ShouldThrowNotFoundException() throws Exception {
        when(customersService.updateCustomer(any(CustomerRequestModel.class), eq(CUSTOMER_ID)))
                .thenThrow(new NotFoundException("Customer not found"));

        mockMvc.perform(put("/api/v1/customers/{customerId}", CUSTOMER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"emailAddress\":\"john.doe@example.com\",\"contactMethodPreference\":\"EMAIL\",\"streetAddress\":\"123 Main St\",\"city\":\"City\",\"province\":\"Province\",\"country\":\"Country\",\"postalCode\":\"12345\",\"phoneNumbers\":[{\"type\":\"MOBILE\",\"number\":\"123-456-7890\"}]}"))
                .andExpect(status().isNotFound());

        verify(customersService, times(1)).updateCustomer(any(CustomerRequestModel.class), eq(CUSTOMER_ID));
    }

    @Test
    void deleteCustomer_WithValidId_ShouldDeleteCustomer() throws Exception {
        doNothing().when(customersService).removeCustomer(CUSTOMER_ID);

        mockMvc.perform(delete("/api/v1/customers/{customerId}", CUSTOMER_ID))
                .andExpect(status().isNoContent());

        verify(customersService, times(1)).removeCustomer(CUSTOMER_ID);
    }

    @Test
    void deleteCustomer_WithInvalidId_ShouldThrowInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/{customerId}", INVALID_CUSTOMER_ID))
                .andExpect(status().isUnprocessableEntity());

        verify(customersService, never()).removeCustomer(anyString());
    }

    @Test
    void deleteCustomer_CustomerNotFound_ShouldThrowNotFoundException() throws Exception {
        doThrow(new NotFoundException("Customer not found")).when(customersService).removeCustomer(CUSTOMER_ID);

        mockMvc.perform(delete("/api/v1/customers/{customerId}", CUSTOMER_ID))
                .andExpect(status().isNotFound());

        verify(customersService, times(1)).removeCustomer(CUSTOMER_ID);
    }
}