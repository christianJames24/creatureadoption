package com.creatureadoption.customers.presentationlayer;

import com.creatureadoption.customers.dataaccesslayer.ContactMethodPreference;
import com.creatureadoption.customers.dataaccesslayer.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public final class CustomerResponseModel extends RepresentationModel<CustomerResponseModel> {

    String customerId;
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
