package com.creatureadoption.customers.presentationlayer;


import com.creatureadoption.customers.dataaccesslayer.ContactMethodPreference;
import com.creatureadoption.customers.dataaccesslayer.PhoneNumber;
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
