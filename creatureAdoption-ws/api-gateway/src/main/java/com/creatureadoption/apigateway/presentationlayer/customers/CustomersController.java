package com.creatureadoption.apigateway.presentationlayer.customers;

import com.creatureadoption.apigateway.businesslayer.customers.CustomersService;
import com.creatureadoption.apigateway.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public class CustomersController {

    private final CustomersService customersService;
    private static final int UUID_LENGTH = 36;

    public CustomersController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @GetMapping(
            produces = "application/json"
    )
    public ResponseEntity<List<CustomerResponseModel>> getCustomers(@RequestParam(required = false) Map<String, String> queryParams) {
        if (queryParams.containsKey("customerId")) {
            String customerId = queryParams.get("customerId");
            if (customerId != null && customerId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid customerId provided: " + customerId);
            }
        }

        List<CustomerResponseModel> customers = customersService.getCustomers(queryParams);
        for(CustomerResponseModel customer : customers) {
            addSelfLink(customer);
        }

        return ResponseEntity.ok(customers);
    }

    @GetMapping(
            value = "/{customerId}",
            produces = "application/json"
    )
    public ResponseEntity<CustomerResponseModel> getCustomerByCustomerId(@PathVariable String customerId) {
        if (customerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid customerId provided: " + customerId);
        }

        CustomerResponseModel customer = customersService.getCustomerByCustomerId(customerId);
        addSelfLink(customer);

        return ResponseEntity.ok(customer);
    }

    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<CustomerResponseModel> addCustomer(@RequestBody CustomerRequestModel customerRequestModel) {
        CustomerResponseModel customer = customersService.addCustomer(customerRequestModel);
        addSelfLink(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PutMapping(
            value = "/{customerId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<CustomerResponseModel> updateCustomer(@RequestBody CustomerRequestModel customerRequestModel, @PathVariable String customerId) {
        if (customerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid customerId provided: " + customerId);
        }

        CustomerResponseModel customer = customersService.updateCustomer(customerRequestModel, customerId);
        addSelfLink(customer);

        return ResponseEntity.ok(customer);
    }

    @DeleteMapping(
            value = "/{customerId}"
    )
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        if (customerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid customerId provided: " + customerId);
        }

        customersService.removeCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    private void addSelfLink(CustomerResponseModel customer) {
        customer.add(
                linkTo(methodOn(CustomersController.class)
                        .getCustomerByCustomerId(customer.getCustomerId()))
                        .withSelfRel()
        );

        customer.add(
                linkTo(methodOn(CustomersController.class)
                        .getCustomers(Map.of()))
                        .withRel("allCustomers")
        );
    }
}