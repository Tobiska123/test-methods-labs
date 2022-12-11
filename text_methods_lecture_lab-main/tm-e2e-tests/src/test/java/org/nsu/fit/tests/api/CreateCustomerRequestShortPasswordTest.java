package org.nsu.fit.tests.api;

import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.nsu.fit.services.fixtures.ContactFixtureBuilder;
import org.nsu.fit.services.rest.RestClient;
import org.nsu.fit.services.rest.data.AccountTokenPojo;
import org.nsu.fit.services.rest.data.ContactPojo;
import org.nsu.fit.services.rest.data.CustomerPojo;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CreateCustomerRequestShortPasswordTest {
    private RestClient restClient;
    private AccountTokenPojo adminToken;

    @BeforeClass
    private void auth() {
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
    }

    @Test
    @Feature("Administrator can create customer")
    @Severity(SeverityLevel.BLOCKER)
    public void succeedsWith6SymbolPassword() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setPass("abcdef")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNotNull(customerPojo);
    }

    @Test
    @Feature("Administrator can create customer")
    @Severity(SeverityLevel.BLOCKER)
    public void failsWith5SymbolPassword() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setPass("abcde")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNull(customerPojo);
    }
}
