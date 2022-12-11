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

public class CreateCustomerRequestCommonPasswordTest {
    private RestClient restClient;
    private AccountTokenPojo adminToken;

    @BeforeClass
    private void auth() {
        restClient = new RestClient();
        adminToken = restClient.authenticate("admin", "setup");
    }

    @Test(description = "fails with 123qwe password")
    @Feature("Administrator can create customer")
    @Severity(SeverityLevel.BLOCKER)
    public void failsWithCommonPassword1() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setPass("123qwe")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNull(customerPojo);
    }

    @Test(description = "fails with qwerty password")
    @Feature("Administrator can create customer")
    @Severity(SeverityLevel.BLOCKER)
    public void failsWithCommonPassword2() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setPass("qwerty")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNull(customerPojo);
    }

    @Test(description = "fails with 1q2w3e password")
    @Feature("Administrator can create customer")
    @Severity(SeverityLevel.BLOCKER)
    public void failsWithCommonPassword3() {
        ContactPojo contactPojo = new ContactFixtureBuilder()
                .setPass("1q2w3e")
                .build();

        CustomerPojo customerPojo = restClient.createCustomer(adminToken, contactPojo);
        Assert.assertNull(customerPojo);
    }
}
