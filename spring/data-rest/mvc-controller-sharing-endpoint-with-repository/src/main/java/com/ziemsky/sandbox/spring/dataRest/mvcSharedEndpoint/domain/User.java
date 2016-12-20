package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class User {

    @Id
    @GeneratedValue
    private String id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    private User() {
        // required by Jackson
    }

    public User(final String firstName, final String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


}
