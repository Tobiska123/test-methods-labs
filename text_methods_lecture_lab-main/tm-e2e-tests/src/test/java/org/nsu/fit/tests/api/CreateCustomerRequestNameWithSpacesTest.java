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

public class CreateCustomerRequestNameWithSpacesTest {
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
    public void failsWithFirstNameWithSpaces() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setFirstName("Jo h n")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNull(customerPojo);
    }

    @Test
    @Feature("Administrator can create customer")
    @Severity(SeverityLevel.BLOCKER)
    public void failsWithLastNameWithSpaces() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setLastName("W i ck")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNull(customerPojo);
    }
}
