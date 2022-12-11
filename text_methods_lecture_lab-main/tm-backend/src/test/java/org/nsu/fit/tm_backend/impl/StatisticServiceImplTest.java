package org.nsu.fit.tm_backend.impl;

import org.glassfish.jersey.internal.guava.Sets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nsu.fit.tm_backend.repository.data.CustomerPojo;
import org.nsu.fit.tm_backend.repository.data.SubscriptionPojo;
import org.nsu.fit.tm_backend.service.CustomerService;
import org.nsu.fit.tm_backend.service.StatisticService;
import org.nsu.fit.tm_backend.service.SubscriptionService;
import org.nsu.fit.tm_backend.service.data.StatisticBO;
import org.nsu.fit.tm_backend.service.data.StatisticPerCustomerBO;
import org.nsu.fit.tm_backend.service.impl.CustomerServiceImpl;
import org.nsu.fit.tm_backend.service.impl.StatisticServiceImpl;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


// Лабораторная 2: покрыть unit тестами класс StatisticServiceImpl на 100%.
// Чтобы протестировать метод calculate() используйте Mockito.spy(statisticService) и переопределите метод
// calculate(UUID customerId) чтобы использовать стратегию "разделяй и властвуй".
@ExtendWith(MockitoExtension.class)
public class StatisticServiceImplTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private SubscriptionService subscriptionService;

    @InjectMocks
    private StatisticServiceImpl statisticService;

    @Test
    public void calculateWithCustomersReturnsTheirStatisticsAndOverallStats() {
        //given
        List<CustomerPojo> targetCustomers = Arrays.asList(
                CustomerObjectMother.createCustomer(100),
                CustomerObjectMother.createCustomer(50)
        );

        Map<UUID, StatisticPerCustomerBO> targetStatistics = new HashMap<>();
        targetStatistics.put(targetCustomers.get(0).id, StatisticPerCustomerObjectMother.createStatisticPerCustomerBO(targetCustomers.get(0)));
        targetStatistics.put(targetCustomers.get(1).id, StatisticPerCustomerObjectMother.createStatisticPerCustomerBO(targetCustomers.get(1)));

        when(customerService.getCustomerIds()).thenReturn(targetCustomers.stream().map(it -> it.id).collect(Collectors.toSet()));

        StatisticService spy = Mockito.spy(statisticService);

        Mockito.doReturn(targetStatistics.get(targetCustomers.get(0).id)).when(spy).calculate(targetCustomers.get(0).id);
        Mockito.doReturn(targetStatistics.get(targetCustomers.get(1).id)).when(spy).calculate(targetCustomers.get(1).id);

        //when
        StatisticBO actual = spy.calculate();

        //then
        verify(spy, times(1)).calculate(targetCustomers.get(0).id);
        verify(spy, times(1)).calculate(targetCustomers.get(1).id);
        assertEquals(100, actual.getOverallFee());
        assertEquals(150, actual.getOverallBalance());
        assertEquals(new HashSet<>(targetStatistics.values()), actual.getCustomers());

    }

    @Test
    public void calculateWithNoRegisteredCustomersReturnsEmptyStatistics() {
        //given
        when(customerService.getCustomerIds()).thenReturn(Collections.emptySet());
        StatisticService spy = Mockito.spy(statisticService);

        //when
        StatisticBO actual = spy.calculate();

        //then
        verify(spy, times(0)).calculate(any());
        assertEquals(0, actual.getOverallFee());
        assertEquals(0, actual.getOverallBalance());
        assertEquals(Collections.emptySet(), actual.getCustomers());
    }

    @Test
    public void calculateSkipsNullStatisticsPerCustomer() {
        //given
        List<CustomerPojo> targetCustomers = Arrays.asList(
                CustomerObjectMother.createCustomer(100),
                CustomerObjectMother.createCustomer(50)
        );

        Map<UUID, StatisticPerCustomerBO> targetStatistics = new HashMap<>();
        targetStatistics.put(targetCustomers.get(0).id, StatisticPerCustomerObjectMother.createStatisticPerCustomerBO(targetCustomers.get(0)));
        targetStatistics.put(targetCustomers.get(1).id, null);

        when(customerService.getCustomerIds()).thenReturn(targetCustomers.stream().map(it -> it.id).collect(Collectors.toSet()));

        StatisticService spy = Mockito.spy(statisticService);

        Mockito.doReturn(targetStatistics.get(targetCustomers.get(0).id)).when(spy).calculate(targetCustomers.get(0).id);
        Mockito.doReturn(targetStatistics.get(targetCustomers.get(1).id)).when(spy).calculate(targetCustomers.get(1).id);

        //when
        StatisticBO actual = spy.calculate();

        //then
        verify(spy, times(1)).calculate(targetCustomers.get(0).id);
        verify(spy, times(1)).calculate(targetCustomers.get(1).id);
        assertEquals(50, actual.getOverallFee());
        assertEquals(100, actual.getOverallBalance());
        assertEquals(
                new HashSet<StatisticPerCustomerBO>() {{ add(targetStatistics.get(targetCustomers.get(0).id)); }},
                actual.getCustomers()
        );
    }

    @Test
    public void calculateForExistedCustomerReturnsStatistics() {
        //given
        CustomerPojo targetCustomer = CustomerObjectMother.createCustomer(100);
        List<SubscriptionPojo> targetSubscriptions = Arrays.asList(
                SubscriptionObjectMother.createSubscription(targetCustomer.id, 50),
                SubscriptionObjectMother.createSubscription(targetCustomer.id, 30)
        );

        when(customerService.lookupCustomer(targetCustomer.id)).thenReturn(targetCustomer);
        when(subscriptionService.getSubscriptions(targetCustomer.id)).thenReturn(targetSubscriptions);

        //when
        StatisticPerCustomerBO actual = statisticService.calculate(targetCustomer.id);

        //then
        assertEquals(100, actual.getOverallBalance());
        assertEquals(80, actual.getOverallFee());
        assertEquals(targetCustomer.id, actual.getCustomerId());
        assertEquals(
                targetSubscriptions.stream().map(it -> it.id).collect(Collectors.toSet()),
                actual.getSubscriptionIds()
                );
    }

    @Test
    public void calculateForNotExistedCustomerReturnsNull() {
        //given
        UUID targetId = UUID.randomUUID();
        when(customerService.lookupCustomer(targetId)).thenReturn(null);

        //when
        StatisticPerCustomerBO actual = statisticService.calculate(targetId);

        //then
        assertNull(actual);
    }

    @Test
    public void calculateForCustomerWithNoSubscriptionsReturnsStatisticsWithEmptySubscriptionsAndNoFee() {
        //given
        CustomerPojo targetCustomer = CustomerObjectMother.createCustomer(100);
        List<SubscriptionPojo> targetSubscriptions = Collections.emptyList();

        when(customerService.lookupCustomer(targetCustomer.id)).thenReturn(targetCustomer);
        when(subscriptionService.getSubscriptions(targetCustomer.id)).thenReturn(targetSubscriptions);

        //when
        StatisticPerCustomerBO actual = statisticService.calculate(targetCustomer.id);

        //then
        assertEquals(100, actual.getOverallBalance());
        assertEquals(0, actual.getOverallFee());
        assertEquals(targetCustomer.id, actual.getCustomerId());
        assertEquals(
                Collections.emptySet(),
                actual.getSubscriptionIds()
        );
    }
}

class CustomerObjectMother {
    static CustomerPojo createCustomer(int balance) {
        CustomerPojo customerPojo = new CustomerPojo();
        customerPojo.id = UUID.randomUUID();
        customerPojo.firstName = "John";
        customerPojo.lastName = "Wick";
        customerPojo.login = "john_wick@example.com";
        customerPojo.pass = "Baba_Jaga";
        customerPojo.balance = balance;
        return customerPojo;
    }
}

class SubscriptionObjectMother {
    static SubscriptionPojo createSubscription(UUID customerId, int fee) {
        SubscriptionPojo subscriptionPojo = new SubscriptionPojo();
        subscriptionPojo.id = UUID.randomUUID();
        subscriptionPojo.customerId = customerId;
        subscriptionPojo.planId = UUID.randomUUID();
        subscriptionPojo.planName = "test_sub";
        subscriptionPojo.planDetails = "test";
        subscriptionPojo.planFee = fee;
        return subscriptionPojo;
    }
}

class StatisticPerCustomerObjectMother {
    static StatisticPerCustomerBO createStatisticPerCustomerBO(CustomerPojo customer) {
        return new StatisticPerCustomerBO(
                customer.id,
                new HashSet<UUID>() {{
                    add(UUID.randomUUID());
                    add(UUID.randomUUID());
                }},
                customer.balance,
                50
        );
    }
}
