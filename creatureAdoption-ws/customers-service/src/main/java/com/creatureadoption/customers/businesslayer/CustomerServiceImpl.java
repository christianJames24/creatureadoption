package com.creatureadoption.customers.businesslayer;


import com.creatureadoption.customers.dataaccesslayer.Customer;
import com.creatureadoption.customers.dataaccesslayer.CustomerAddress;
import com.creatureadoption.customers.dataaccesslayer.CustomerIdentifier;
import com.creatureadoption.customers.dataaccesslayer.CustomerRepository;
import com.creatureadoption.customers.mappinglayer.CustomerRequestMapper;
import com.creatureadoption.customers.mappinglayer.CustomerResponseMapper;
import com.creatureadoption.customers.presentationlayer.CustomerRequestModel;
import com.creatureadoption.customers.presentationlayer.CustomerResponseModel;
import com.creatureadoption.customers.utils.exceptions.DuplicateEmailException;
import com.creatureadoption.customers.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerResponseMapper customerResponseMapper;
    private final CustomerRequestMapper customerRequestMapper;


    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerResponseMapper customerResponseMapper, CustomerRequestMapper customerRequestMapper) {
        this.customerRepository = customerRepository;
        this.customerResponseMapper = customerResponseMapper;
        this.customerRequestMapper = customerRequestMapper;
    }

    @Override
    public List<CustomerResponseModel> getCustomers(Map<String, String> queryParams) {
        List<Customer> customers = customerRepository.findAll();

        String customerId = queryParams.get("customerId");
        String firstName = queryParams.get("firstName");
        String lastName = queryParams.get("lastName");
        String contactMethodPreference = queryParams.get("contactMethodPreference");
        String country = queryParams.get("country");
        String province = queryParams.get("province");
        String city = queryParams.get("city");

        if (contactMethodPreference != null && !contactMethodPreference.isEmpty()) {
            customers = customers.stream()
                    .filter(c -> c.getContactMethodPreference() != null &&
                            c.getContactMethodPreference().toString().equalsIgnoreCase(contactMethodPreference))
                    .collect(Collectors.toList());
        }
        if (country != null && !country.isEmpty()) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerAddress() != null &&
                            c.getCustomerAddress().getCountry().equalsIgnoreCase(country))
                    .collect(Collectors.toList());
        }
        if (province != null && !province.isEmpty()) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerAddress() != null &&
                            c.getCustomerAddress().getProvince().equalsIgnoreCase(province))
                    .collect(Collectors.toList());
        }
        if (city != null && !city.isEmpty()) {
            customers = customers.stream()
                    .filter(c -> c.getCustomerAddress() != null &&
                            c.getCustomerAddress().getCity().equalsIgnoreCase(city))
                    .collect(Collectors.toList());
        }


        return customerResponseMapper.entityListToResponseModelList(customers);
    }


    @Override
    public CustomerResponseModel getCustomerByCustomerId(String customerId) {
        Customer customer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);

        if (customer == null) {
            throw new NotFoundException("Provided customerId not found: " + customerId);
        }
        return customerResponseMapper.entityToResponseModel(customer);
    }

    @Override
    public CustomerResponseModel addCustomer(CustomerRequestModel customerRequestModel) {
        List<Customer> existingCustomers = customerRepository.findAll().stream() //subdomain specific exception
                .filter(c -> c.getEmailAddress().equalsIgnoreCase(customerRequestModel.getEmailAddress()))
                .collect(Collectors.toList());

        if (!existingCustomers.isEmpty()) {
            throw new DuplicateEmailException("A customer with email " + customerRequestModel.getEmailAddress() + " already exists");
        }

        CustomerAddress address = new CustomerAddress(customerRequestModel.getStreetAddress(), customerRequestModel.getCity(),
                customerRequestModel.getProvince(), customerRequestModel.getCountry(), customerRequestModel.getPostalCode());

        Customer customer = customerRequestMapper.requestModelToEntity(customerRequestModel, new CustomerIdentifier(), address);

        customer.setCustomerAddress(address);
        return customerResponseMapper.entityToResponseModel(customerRepository.save(customer));
    }

    @Override
    public CustomerResponseModel updateCustomer(CustomerRequestModel customerRequestModel, String customerId) {

        Customer existingCustomer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);

        //check if a customer with the provided UUID exists in the system. If it doesn't, return null
        //later, when we implement exception handling, we'll return an exception
        if (existingCustomer == null) {
            throw new NotFoundException("Provided customerId not found: " + customerId);
        }
        CustomerAddress address = new CustomerAddress(customerRequestModel.getStreetAddress(), customerRequestModel.getCity(),
            customerRequestModel.getProvince(), customerRequestModel.getCountry(), customerRequestModel.getPostalCode());
        Customer updatedCustomer = customerRequestMapper.requestModelToEntity(customerRequestModel,
            existingCustomer.getCustomerIdentifier(), address);
        updatedCustomer.setId(existingCustomer.getId());

        List<Customer> existingCustomers = customerRepository.findAll().stream() //subdomain specific exception
                .filter(c -> c.getEmailAddress().equalsIgnoreCase(customerRequestModel.getEmailAddress()))
                .collect(Collectors.toList());
        if (!existingCustomers.isEmpty()) {
            throw new DuplicateEmailException("A customer with email " + customerRequestModel.getEmailAddress() + " already exists");
        }

        Customer response = customerRepository.save(updatedCustomer);
        return customerResponseMapper.entityToResponseModel(response);
    }

    @Override
    public void removeCustomer(String customerId) {
        Customer existingCustomer = customerRepository.findByCustomerIdentifier_CustomerId(customerId);

        if (existingCustomer == null) {
            throw new NotFoundException("Provided customerId not found: " + customerId);
        }

        customerRepository.delete(existingCustomer);
    }
}
