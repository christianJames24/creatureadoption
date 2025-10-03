package com.creatureadoption.apigateway.businesslayer.customers;

import com.creatureadoption.apigateway.domainclientlayer.adoptions.AdoptionsServiceClient;
import com.creatureadoption.apigateway.domainclientlayer.customers.CustomersServiceClient;
import com.creatureadoption.apigateway.presentationlayer.adoptions.AdoptionResponseModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerResponseModel;
import com.creatureadoption.apigateway.utils.exceptions.EntityInUseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CustomersServiceImpl implements CustomersService {

    private final CustomersServiceClient customersServiceClient;
    private final AdoptionsServiceClient adoptionsServiceClient;

    public CustomersServiceImpl(CustomersServiceClient customersServiceClient, AdoptionsServiceClient adoptionsServiceClient) {
        this.customersServiceClient = customersServiceClient;
        this.adoptionsServiceClient = adoptionsServiceClient;
    }

    @Override
    public List<CustomerResponseModel> getCustomers(Map<String, String> queryParams) {
        return customersServiceClient.getCustomers(queryParams);
    }

    @Override
    public CustomerResponseModel getCustomerByCustomerId(String customerId) {
        return customersServiceClient.getCustomerByCustomerId(customerId);
    }

    @Override
    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        return customersServiceClient.addCustomer(customerRequestModel);
    }

    @Override
    public CustomerResponseModel updateCustomer(CustomerRequestModel customerRequestModel, String customerId) {
        return customersServiceClient.updateCustomer(customerRequestModel, customerId);
    }

    @Override
    public void removeCustomer(String customerId) {
        // Check if customer has any adoptions
        Map<String, String> queryParams = Map.of("customerId", customerId);
        List<AdoptionResponseModel> adoptions = adoptionsServiceClient.getAdoptions(queryParams);

        if (!adoptions.isEmpty()) {
            throw new EntityInUseException("Cannot delete customer with ID: " + customerId +
                    " because they have existing adoptions");
        }

        customersServiceClient.removeCustomer(customerId);
    }
}