package com.creatureadoption.customers.mappinglayer;

import com.creatureadoption.customers.dataaccesslayer.Customer;
import com.creatureadoption.customers.dataaccesslayer.CustomerAddress;
import com.creatureadoption.customers.dataaccesslayer.CustomerIdentifier;
import com.creatureadoption.customers.presentationlayer.CustomerRequestModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CustomerRequestMapper {

    @Mappings({
        @Mapping(target = "id", ignore = true),
    })
    Customer requestModelToEntity(CustomerRequestModel customerRequestModel, CustomerIdentifier customerIdentifier,
                                  CustomerAddress customerAddress);
}
