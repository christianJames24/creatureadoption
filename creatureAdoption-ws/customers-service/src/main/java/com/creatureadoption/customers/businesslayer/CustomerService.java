package com.creatureadoption.customers.businesslayer;


import com.creatureadoption.customers.presentationlayer.CustomerRequestModel;
import com.creatureadoption.customers.presentationlayer.CustomerResponseModel;

import java.util.List;
import java.util.Map;

public interface CustomerService {

    List<CustomerResponseModel> getCustomers(Map<String, String> queryParams);
    CustomerResponseModel getCustomerByCustomerId(String customerId);
    CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel);
    CustomerResponseModel updateCustomer(CustomerRequestModel updatedCustomer, String customerId);
    void removeCustomer(String customerId);
}
