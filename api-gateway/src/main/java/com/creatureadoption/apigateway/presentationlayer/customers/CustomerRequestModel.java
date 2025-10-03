package com.creatureadoption.apigateway.presentationlayer.customers;


import com.creatureadoption.apigateway.domainclientlayer.customers.ContactMethodPreference;
import com.creatureadoption.apigateway.domainclientlayer.customers.PhoneNumber;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerRequestModel {

    String firstName;
    String lastName;
    String emailAddress;
    ContactMethodPreference contactMethodPreference;
    String streetAddress;
    String city;
    String province;
    String country;
    String postalCode;
    List<PhoneNumber> phoneNumbers;

}
