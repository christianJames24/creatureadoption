package com.creatureadoption.adoptions.domainclientlayer.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseModel {
    private String customerId;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String contactMethodPreference;
    private String streetAddress;
    private String city;
    private String province;
    private String country;
    private String postalCode;
}