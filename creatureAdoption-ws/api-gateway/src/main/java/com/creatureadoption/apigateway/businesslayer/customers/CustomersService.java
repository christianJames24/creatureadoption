package com.creatureadoption.apigateway.businesslayer.customers;


import com.creatureadoption.apigateway.presentationlayer.customers.CustomerRequestModel;
import com.creatureadoption.apigateway.presentationlayer.customers.CustomerResponseModel;

import java.util.List;
import java.util.Map;

public interface CustomersService {
    List<CustomerResponseModel> getCustomers(Map<String, String> queryParams);
    CustomerResponseModel getCustomerByCustomerId(String customerId);
    CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel);
    CustomerResponseModel updateCustomer(CustomerRequestModel customerRequestModel, String customerId);
    void removeCustomer(String customerId);
}