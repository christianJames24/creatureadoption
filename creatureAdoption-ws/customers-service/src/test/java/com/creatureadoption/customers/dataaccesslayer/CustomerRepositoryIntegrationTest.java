package com.creatureadoption.customers.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class CustomerRepositoryIntegrationTest {

    @Autowired
    private CustomerRepository repository;

    private Customer savedEntity;
    private CustomerIdentifier identifier;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();

        //so i dont make data in every thing
        identifier = new CustomerIdentifier();
        CustomerAddress address = new CustomerAddress(
                "123 Test St",
                "Test City",
                "Test Province",
                "Test Country",
                "12345"
        );

        PhoneNumber phone = new PhoneNumber(PhoneType.MOBILE, "1234567890");
        List<PhoneNumber> phones = new ArrayList<>();
        phones.add(phone);

        Customer entity = new Customer(
                "John",
                "Doe",
                "john.doe@example.com",
                ContactMethodPreference.EMAIL,
                address,
                phones
        );
        entity.setCustomerIdentifier(identifier);

        savedEntity = repository.save(entity);

        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
    }

    @Test
    public void whenValidCustomerId_thenCustomerShouldBeFound() {
        //arrange
        String customerId = savedEntity.getCustomerIdentifier().getCustomerId();

        //act
        Customer found = repository.findByCustomerIdentifier_CustomerId(customerId);

        //assert
        assertThat(found).isNotNull();
        assertThat(found.getCustomerIdentifier().getCustomerId()).isEqualTo(customerId);
        assertThat(found.getFirstName()).isEqualTo(savedEntity.getFirstName());
        assertThat(found.getLastName()).isEqualTo(savedEntity.getLastName());
    }

    @Test
    public void whenInvalidCustomerId_thenCustomerShouldNotBeFound() {
        //arrange
        String invalidId = "invalidId";

        //act
        Customer found = repository.findByCustomerIdentifier_CustomerId(invalidId);

        //assert
        assertThat(found).isNull();
    }

    @Test
    public void whenSaveNewCustomer_thenItShouldBePersisted() {
        //arrange
        CustomerIdentifier newIdentifier = new CustomerIdentifier();
        CustomerAddress address = new CustomerAddress(
                "456 New St",
                "New City",
                "New Province",
                "New Country",
                "67890"
        );

        PhoneNumber phone = new PhoneNumber(PhoneType.HOME, "9876543210");
        List<PhoneNumber> phones = new ArrayList<>();
        phones.add(phone);

        Customer newEntity = new Customer(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                ContactMethodPreference.PHONE,
                address,
                phones
        );
        newEntity.setCustomerIdentifier(newIdentifier);

        //act
        Customer saved = repository.save(newEntity);

        //assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Jane");

        //see if the created data can be retrieved
        Customer retrieved = repository.findByCustomerIdentifier_CustomerId(newIdentifier.getCustomerId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCustomerIdentifier().getCustomerId()).isEqualTo(newIdentifier.getCustomerId());
    }

    @Test
    public void whenDeleteCustomer_thenItShouldBeRemoved() {
        //arrange
        String customerId = savedEntity.getCustomerIdentifier().getCustomerId();

        //act
        repository.delete(savedEntity);

        //assert
        Customer found = repository.findByCustomerIdentifier_CustomerId(customerId);
        assertThat(found).isNull();
    }

    @Test
    public void whenFindAll_thenAllCustomersShouldBeReturned() {
        //arrange
        CustomerIdentifier newIdentifier = new CustomerIdentifier();
        CustomerAddress address = new CustomerAddress(
                "456 New St",
                "New City",
                "New Province",
                "New Country",
                "67890"
        );

        PhoneNumber phone = new PhoneNumber(PhoneType.HOME, "9876543210");
        List<PhoneNumber> phones = new ArrayList<>();
        phones.add(phone);

        Customer newEntity = new Customer(
                "Jane",
                "Smith",
                "jane.smith@example.com",
                ContactMethodPreference.PHONE,
                address,
                phones
        );
        newEntity.setCustomerIdentifier(newIdentifier);
        repository.save(newEntity);

        //act
        List<Customer> customers = repository.findAll();

        //assert
        assertThat(customers).isNotNull();
        assertThat(customers.size()).isEqualTo(2);
    }

    @Test
    public void whenUpdateCustomer_thenChangesAreSaved() {
        // arrange
        String customerId = savedEntity.getCustomerIdentifier().getCustomerId();
        Customer toUpdate = repository.findByCustomerIdentifier_CustomerId(customerId);
        toUpdate.setFirstName("UpdatedFirst");
        toUpdate.setLastName("UpdatedLast");

        // act
        Customer updated = repository.save(toUpdate);

        // assert
        assertThat(updated).isNotNull();
        assertThat(updated.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(updated.getLastName()).isEqualTo("UpdatedLast");

        // verify from fresh query
        Customer retrieved = repository.findByCustomerIdentifier_CustomerId(customerId);
        assertThat(retrieved.getFirstName()).isEqualTo("UpdatedFirst");
        assertThat(retrieved.getLastName()).isEqualTo("UpdatedLast");
    }

    @Test
    public void testCustomerIdentifierCreation() {
        // arrange & act
        CustomerIdentifier identifier1 = new CustomerIdentifier();
        CustomerIdentifier identifier2 = new CustomerIdentifier();

        // assert
        assertThat(identifier1.getCustomerId()).isNotNull();

        // verify uniqueness
        assertThat(identifier1.getCustomerId()).isNotEqualTo(identifier2.getCustomerId());
    }

    @Test
    public void testCustomerAddressCreation() {
        // arrange & act
        CustomerAddress address = new CustomerAddress(
                "789 Test St",
                "Test City 2",
                "Test Province 2",
                "Test Country 2",
                "54321"
        );

        // assert
        assertThat(address.getStreetAddress()).isEqualTo("789 Test St");
        assertThat(address.getCity()).isEqualTo("Test City 2");
        assertThat(address.getProvince()).isEqualTo("Test Province 2");
        assertThat(address.getCountry()).isEqualTo("Test Country 2");
        assertThat(address.getPostalCode()).isEqualTo("54321");
    }

    @Test
    public void testPhoneNumberCreation() {
        // arrange & act
        PhoneNumber phoneNumber = new PhoneNumber(PhoneType.WORK, "555-1234");

        // assert
        assertThat(phoneNumber.getType()).isEqualTo(PhoneType.WORK);
        assertThat(phoneNumber.getNumber()).isEqualTo("555-1234");
    }

    @Test
    public void testCustomerFullConstructor() {
        // arrange
        CustomerAddress address = new CustomerAddress(
                "555 Main St",
                "Main City",
                "Main Province",
                "Main Country",
                "M4IN"
        );

        PhoneNumber phone1 = new PhoneNumber(PhoneType.MOBILE, "123-456-7890");
        PhoneNumber phone2 = new PhoneNumber(PhoneType.HOME, "098-765-4321");
        List<PhoneNumber> phones = new ArrayList<>();
        phones.add(phone1);
        phones.add(phone2);

        // act
        Customer customer = new Customer(
                "ConstructorTest",
                "TestingLastName",
                "test@constructor.com",
                ContactMethodPreference.TEXT,
                address,
                phones
        );

        // assert
        assertThat(customer.getFirstName()).isEqualTo("ConstructorTest");
        assertThat(customer.getLastName()).isEqualTo("TestingLastName");
        assertThat(customer.getEmailAddress()).isEqualTo("test@constructor.com");
        assertThat(customer.getContactMethodPreference()).isEqualTo(ContactMethodPreference.TEXT);
        assertThat(customer.getCustomerAddress()).isEqualTo(address);
        assertThat(customer.getPhoneNumbers()).isEqualTo(phones);
        assertThat(customer.getPhoneNumbers().size()).isEqualTo(2);
    }

    @Test
    public void testCustomerNoArgsConstructor() {
        // act
        Customer customer = new Customer();

        // assert
        assertThat(customer).isNotNull();
    }

    @Test
    public void testCustomerAddressNoArgsConstructor() {
        // act
        CustomerAddress address = new CustomerAddress();

        // assert
        assertThat(address).isNotNull();
    }

    @Test
    public void testCustomerAddressGetters() {
        CustomerAddress address = new CustomerAddress(
                "123 Main St",
                "Test City",
                "Test Province",
                "Test Country",
                "12345"
        );

        // Test each getter individually to ensure coverage
        assertThat(address.getStreetAddress()).isEqualTo("123 Main St");
        assertThat(address.getCity()).isEqualTo("Test City");
        assertThat(address.getProvince()).isEqualTo("Test Province");
        assertThat(address.getCountry()).isEqualTo("Test Country");
        assertThat(address.getPostalCode()).isEqualTo("12345");

        // Test the no-args constructor
        CustomerAddress emptyAddress = new CustomerAddress();
        assertThat(emptyAddress).isNotNull();

        // Test CustomerIdentifier with explicit ID
        String customId = "test-id";
        CustomerIdentifier identifier = new CustomerIdentifier(customId);
        assertThat(identifier.getCustomerId()).isEqualTo(customId);
    }
}