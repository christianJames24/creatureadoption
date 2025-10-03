package com.creatureadoption.customers.mappinglayer;

import com.creatureadoption.customers.dataaccesslayer.Customer;
import com.creatureadoption.customers.presentationlayer.CustomerController;
import com.creatureadoption.customers.presentationlayer.CustomerResponseModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.hateoas.Link;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Mapper(componentModel = "spring")
public interface CustomerResponseMapper {

    @Mapping(expression = "java(customer.getCustomerIdentifier().getCustomerId())", target = "customerId")
    @Mapping(expression = "java(customer.getCustomerAddress().getStreetAddress())", target = "streetAddress")
    @Mapping(expression = "java(customer.getCustomerAddress().getCity())", target = "city")
    @Mapping(expression = "java(customer.getCustomerAddress().getProvince())", target = "province")
    @Mapping(expression = "java(customer.getCustomerAddress().getCountry())", target = "country")
    @Mapping(expression = "java(customer.getCustomerAddress().getPostalCode())", target = "postalCode")
    CustomerResponseModel entityToResponseModel(Customer customer);

    List<CustomerResponseModel> entityListToResponseModelList(List<Customer> customers);

    @AfterMapping
    default void addLinks(@MappingTarget CustomerResponseModel response, Customer customer) {
        Link selfLink = linkTo(methodOn(CustomerController.class)
                .getCustomerByCustomerId(response.getCustomerId()))
                .withSelfRel();
        response.add(selfLink);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("customerId", response.getCustomerId());

        Link allCustomersLink = linkTo(methodOn(CustomerController.class)
                .getCustomers(queryParams))
                .withRel("allCustomers");
        response.add(allCustomersLink);
    }
}
