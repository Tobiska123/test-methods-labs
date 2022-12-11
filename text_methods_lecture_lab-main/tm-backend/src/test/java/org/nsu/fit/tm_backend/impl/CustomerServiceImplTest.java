package org.nsu.fit.tm_backend.impl;

import java.util.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nsu.fit.tm_backend.repository.Repository;
import org.nsu.fit.tm_backend.repository.data.ContactPojo;
import org.nsu.fit.tm_backend.repository.data.CustomerPojo;
import org.nsu.fit.tm_backend.repository.data.SubscriptionPojo;
import org.nsu.fit.tm_backend.service.data.StatisticPerCustomerBO;
import org.nsu.fit.tm_backend.service.impl.CustomerServiceImpl;
import org.nsu.fit.tm_backend.service.impl.auth.data.AuthenticatedUserDetails;
import org.nsu.fit.tm_backend.shared.Authority;
import org.nsu.fit.tm_backend.shared.Globals;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Лабораторная 2: покрыть unit тестами класс CustomerServiceImpl на 100%.
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {
    @Mock
    private Repository repository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void testCreateCustomer() {
        // arrange: готовим входные аргументы и настраиваем mock'и.
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = "Baba_Jaga";
        createCustomerInput.balance = 0;

        CustomerPojo createCustomerOutput = new CustomerPojo();
        createCustomerOutput.id = UUID.randomUUID();
        createCustomerOutput.firstName = "John";
        createCustomerOutput.lastName = "Wick";
        createCustomerOutput.login = "john_wick@example.com";
        createCustomerOutput.pass = "Baba_Jaga";
        createCustomerOutput.balance = 0;

        when(repository.createCustomer(createCustomerInput)).thenReturn(createCustomerOutput); // any()

        // act: вызываем метод, который хотим протестировать.
        CustomerPojo customer = customerService.createCustomer(createCustomerInput);

        // assert: проверяем результат выполнения метода.
        assertEquals(customer.id, createCustomerOutput.id);

        // Проверяем, что метод по созданию Customer был вызван ровно 1 раз с определенными аргументами
        verify(repository, times(1)).createCustomer(createCustomerInput);

        // Проверяем, что другие методы не вызывались...
        verify(repository, times(0)).getCustomers();
    }

    @Test
    void testCreateCustomerWithNullArgument() {
        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                customerService.createCustomer(null));
        assertEquals("Argument 'customer' is null.", exception.getMessage());
    }

    @Test
    void testCreateCustomerWithEasyPassword() {
        // arrange
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = "123qwe"; // easy
        createCustomerInput.balance = 0;

        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Password is very easy.", exception.getMessage());
    }

    @Test
    void testCreateCustomerWithNullPassword() {
        // arrange
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = null; // null
        createCustomerInput.balance = 0;

        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Field 'customer.pass' is null.", exception.getMessage());
    }

    @Test
    void testCreateCustomerWithShortPassword() {
        // arrange
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = "q1w2e"; // short
        createCustomerInput.balance = 0;

        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Password's length should be more or equal 6 symbols and less or equal 12 symbols.", exception.getMessage());
    }

    @Test
    void testCreateCustomerWithNullLogin() {
        // arrange
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = null; // null
        createCustomerInput.pass = "f712da9Jh;-";
        createCustomerInput.balance = 0;

        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Field 'customer.login' is null.", exception.getMessage());
    }

    @Test
    void testCreateCustomerWithTakenLogin() {
        // arrange
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = "john_wick@example.com";
        createCustomerInput.pass = "Baba_Jaga";
        createCustomerInput.balance = 0;

        CustomerPojo createCustomerOutput = new CustomerPojo();
        createCustomerOutput.id = UUID.randomUUID();
        createCustomerOutput.firstName = "John";
        createCustomerOutput.lastName = "Wick";
        createCustomerOutput.login = "john_wick@example.com";
        createCustomerOutput.pass = "Baba_Jaga";
        createCustomerOutput.balance = 0;

        when(repository.getCustomerByLogin(createCustomerInput.login)).thenReturn(createCustomerOutput); // any()

        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Provided login is already in use.", exception.getMessage());
    }

    @Test
    void testCreateCustomerWithInvalidLogin() {
        // arrange
        CustomerPojo createCustomerInput = new CustomerPojo();
        createCustomerInput.firstName = "John";
        createCustomerInput.lastName = "Wick";
        createCustomerInput.login = ".john_wick@example.com"; // invalid login
        createCustomerInput.pass = "f712da9Jh;-";
        createCustomerInput.balance = 0;

        // act-assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> customerService.createCustomer(createCustomerInput));
        assertEquals("Provided login/email contains forbidden symbols.", exception.getMessage());
    }

    @Test
    void testGetCustomers() {
        // arrange
        CustomerPojo customerPojo1 = new CustomerPojo();
        customerPojo1.firstName = "John";
        customerPojo1.lastName = "Wick";
        customerPojo1.login = "john_wick@example.com";
        customerPojo1.pass = "a23afd01-";
        customerPojo1.balance = 0;

        CustomerPojo customerPojo2 = new CustomerPojo();
        customerPojo2.firstName = "Steven";
        customerPojo2.lastName = "Flick";
        customerPojo2.login = "Steven_Flick@example.com";
        customerPojo2.pass = "AGsd303=-";
        customerPojo2.balance = 0;

        HashSet<CustomerPojo> getCustomersOutput = new HashSet<>();
        getCustomersOutput.add(customerPojo1);
        getCustomersOutput.add(customerPojo2);

        when(repository.getCustomers()).thenReturn(getCustomersOutput);

        // act: вызываем метод, который хотим протестировать.
        Set<CustomerPojo> customers = customerService.getCustomers();

        // assert: проверяем результат выполнения метода.
        assertEquals(2, customers.size());
        assertTrue(customers.containsAll(getCustomersOutput));

        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).deleteCustomer(any());
    }

    @Test
    void testGetCustomerIds() {
        // arrange
        CustomerPojo customerPojo1 = new CustomerPojo();
        customerPojo1.id = UUID.randomUUID();
        customerPojo1.firstName = "John";
        customerPojo1.lastName = "Wick";
        customerPojo1.login = "john_wick@example.com";
        customerPojo1.pass = "a23afd01-";
        customerPojo1.balance = 0;

        CustomerPojo customerPojo2 = new CustomerPojo();
        customerPojo2.id = UUID.randomUUID();
        customerPojo2.firstName = "Steven";
        customerPojo2.lastName = "Flick";
        customerPojo2.login = "Steven_Flick@example.com";
        customerPojo2.pass = "AGsd303=-";
        customerPojo2.balance = 0;

        HashSet<UUID> getCustomerIdsOutput = new HashSet<>();
        getCustomerIdsOutput.add(customerPojo1.id);
        getCustomerIdsOutput.add(customerPojo2.id);

        when(repository.getCustomerIds()).thenReturn(getCustomerIdsOutput);

        // act: вызываем метод, который хотим протестировать.
        Set<UUID> customerIds = customerService.getCustomerIds();

        // assert: проверяем результат выполнения метода.
        assertEquals(2, customerIds.size());
        assertTrue(customerIds.containsAll(getCustomerIdsOutput));

        verify(repository, times(1)).getCustomerIds();
        verify(repository, times(0)).deleteCustomer(any());
    }

    @Test
    void testGetCustomer() {
        // arrange
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = UUID.randomUUID();
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "john_wick@example.com";
        customerPojo.pass = "a23afd01-";
        customerPojo.balance = 0;

        when(repository.getCustomer(customerPojo.id)).thenReturn(customerPojo);

        // act: вызываем метод, который хотим протестировать.
        CustomerPojo customer = customerService.getCustomer(customerPojo.id);

        // assert: проверяем результат выполнения метода.
        assertEquals(customerPojo, customer);

        verify(repository, times(1)).getCustomer(any());
        verify(repository, times(0)).deleteCustomer(any());
    }

    @Test
    void testLookupCustomerUUID() {
        // arrange
        CustomerPojo customerPojo1 = new CustomerPojo();
        customerPojo1.id = UUID.randomUUID();
        customerPojo1.firstName = "John";
        customerPojo1.lastName = "Wick";
        customerPojo1.login = "john_wick@example.com";
        customerPojo1.pass = "a23afd01-";
        customerPojo1.balance = 0;

        CustomerPojo customerPojo2 = new CustomerPojo();
        customerPojo2.id = UUID.randomUUID();
        customerPojo2.firstName = "Steven";
        customerPojo2.lastName = "Flick";
        customerPojo2.login = "Steven_Flick@example.com";
        customerPojo2.pass = "AGsd303=-";
        customerPojo2.balance = 0;

        HashSet<CustomerPojo> getCustomersOutput = new HashSet<>();
        getCustomersOutput.add(customerPojo1);
        getCustomersOutput.add(customerPojo2);

        when(repository.getCustomers()).thenReturn(getCustomersOutput);

        // act: вызываем метод, который хотим протестировать.
        CustomerPojo customer = customerService.lookupCustomer(customerPojo1.id);

        // assert: проверяем результат выполнения метода.
        assertEquals(customerPojo1, customer);

        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).deleteCustomer(any());
    }

    @Test
    void testLookupCustomerLogin() {
        // arrange
        CustomerPojo customerPojo1 = new CustomerPojo();
        customerPojo1.id = UUID.randomUUID();
        customerPojo1.firstName = "John";
        customerPojo1.lastName = "Wick";
        customerPojo1.login = "john_wick@example.com";
        customerPojo1.pass = "a23afd01-";
        customerPojo1.balance = 0;

        CustomerPojo customerPojo2 = new CustomerPojo();
        customerPojo2.id = UUID.randomUUID();
        customerPojo2.firstName = "Steven";
        customerPojo2.lastName = "Flick";
        customerPojo2.login = "Steven_Flick@example.com";
        customerPojo2.pass = "AGsd303=-";
        customerPojo2.balance = 0;

        HashSet<CustomerPojo> getCustomersOutput = new HashSet<>();
        getCustomersOutput.add(customerPojo1);
        getCustomersOutput.add(customerPojo2);

        when(repository.getCustomers()).thenReturn(getCustomersOutput);

        // act: вызываем метод, который хотим протестировать.
        CustomerPojo customer = customerService.lookupCustomer(customerPojo1.login);

        // assert: проверяем результат выполнения метода.
        assertEquals(customerPojo1, customer);

        verify(repository, times(1)).getCustomers();
        verify(repository, times(0)).deleteCustomer(any());
    }

    @Test
    void testDeleteCustomer() {
        // arrange
        UUID uuid = UUID.randomUUID();

        // act: вызываем метод, который хотим протестировать.
        customerService.deleteCustomer(uuid);

        // assert: проверяем результат выполнения метода.
        verify(repository, times(1)).deleteCustomer(uuid);
    }

    @Test
    void testTopUpBalance() {
        // arrange
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = UUID.randomUUID();
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "john_wick@example.com";
        customerPojo.pass = "a23afd01-";
        customerPojo.balance = 0;

        when(repository.getCustomer(customerPojo.id)).thenReturn(customerPojo);


        // act: вызываем метод, который хотим протестировать.
        CustomerPojo customerPojoIncreased = customerService.topUpBalance(customerPojo.id, 100);

        // assert: проверяем результат выполнения метода.
        assertEquals(customerPojoIncreased, customerPojo);

        verify(repository, times(1)).editCustomer(customerPojoIncreased);
        verify(repository, times(0)).deleteCustomer(any());
    }

    @Test
    void testMeReturnsOnlyContactInformation() {
        // arrange
        AuthenticatedUserDetails customerDetails = new AuthenticatedUserDetails(
                "1",
                "customer_login",
                new HashSet<String>() {{ add(Authority.CUSTOMER_ROLE); }}
        );

        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = UUID.randomUUID();
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "customer_login";
        customerPojo.pass = "a23afd01-";
        customerPojo.balance = 0;

        ContactPojo expectedContactPojo = new ContactPojo();
        expectedContactPojo.firstName = "John";
        expectedContactPojo.lastName = "Wick";
        expectedContactPojo.login = "customer_login";
        expectedContactPojo.pass = "a23afd01-";
        expectedContactPojo.balance = 0;

        when(repository.getCustomerByLogin("customer_login")).thenReturn(customerPojo);

        // act
        ContactPojo actualContactPojo = customerService.me(customerDetails);

        //assert
        assertEquals(actualContactPojo, expectedContactPojo);
    }

    @Test
    void testMeReturnsEmptyDtoWithLoginForAdmin() {
        // arrange
        AuthenticatedUserDetails adminDetails = new AuthenticatedUserDetails(
                "1",
                "admin",
                new HashSet<String>() {{ add(Authority.ADMIN_ROLE); }}
                );

        // act
        ContactPojo actualContact = customerService.me(adminDetails);

        // assert
        assertNull(actualContact.firstName);
        assertNull(actualContact.lastName);
        assertEquals(Globals.ADMIN_LOGIN, actualContact.login);
        assertNull(actualContact.pass);
        assertEquals(0, actualContact.balance);
    }
}
