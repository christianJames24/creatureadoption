package com.creatureadoption.customers.presentationlayer;

import com.creatureadoption.customers.businesslayer.CustomerService;
import com.creatureadoption.customers.utils.exceptions.DeleteRequestBodyNotAllowedException;
import com.creatureadoption.customers.utils.exceptions.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private static final int UUID_LENGTH = 36;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseModel>> getCustomers(
            @RequestParam(required = false) Map<String, String> queryParams) {

        if (queryParams.containsKey("customerId")) {
            String customerId = queryParams.get("customerId");
            if (customerId != null && customerId.length() != UUID_LENGTH) {
                throw new InvalidInputException("Invalid customerId provided: " + customerId);
            }
        }
        return ResponseEntity.ok().body(customerService.getCustomers(queryParams));
    }



    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponseModel> getCustomerByCustomerId(@PathVariable String customerId) {
        if (customerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid customerId provided: " + customerId);
        }
        return ResponseEntity.ok().body(customerService.getCustomerByCustomerId(customerId));
    }

    @PostMapping()
    public ResponseEntity<CustomerResponseModel> addCustomer(@RequestBody CustomerRequestModel customerRequestModel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.addCustomer(customerRequestModel));
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerResponseModel> updateCustomer(@RequestBody CustomerRequestModel customerRequestModel, @PathVariable String customerId) {
        if (customerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid customerId provided: " + customerId);
        }
        return ResponseEntity.ok().body(customerService.updateCustomer(customerRequestModel, customerId));
    }

    @DeleteMapping("/{customerId}") //i tried adding custom delete exception but i really do not have that much time left for milestone 2 sorry im tired 😭😭 i//update i did it in the api gateway
    public ResponseEntity<Void> deleteCustomer(
            @PathVariable String customerId,
            @RequestBody(required = false) String body  // ← capture any body
    ) {
        // 1) if there is a body, reject
        if (body != null && !body.trim().isEmpty()) {
            throw new DeleteRequestBodyNotAllowedException(
                    "DELETE must not include a request body"
            );
        }

        // 2) existing validation
        if (customerId.length() != UUID_LENGTH) {
            throw new InvalidInputException("Invalid customerId provided: " + customerId);
        }

        customerService.removeCustomer(customerId);
        return ResponseEntity.noContent().build();
    }
}
