package org.nsu.fit.services.fixtures;

import org.apache.commons.lang3.RandomStringUtils;
import org.nsu.fit.services.rest.data.ContactPojo;

public class ContactFixtureBuilder {
    private String firstName = "John";

    private String lastName = "Wick";

    private String login = RandomStringUtils.randomAlphabetic(10) + "@example.com";

    private String pass = "strongpass";

    private int balance = 0;

    public ContactFixtureBuilder setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ContactFixtureBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public ContactFixtureBuilder setLogin(String login) {
        this.login = login;
        return this;
    }

    public ContactFixtureBuilder setPass(String pass) {
        this.pass = pass;
        return this;
    }

    public ContactFixtureBuilder setBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public ContactPojo build() {
        return new ContactPojo(firstName, lastName, login, pass, balance);
    }
}
