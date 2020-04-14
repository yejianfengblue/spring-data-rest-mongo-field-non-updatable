package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.readonlyproperty;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Field username is annotated with {@link ReadOnlyProperty}
 */
@Document("readOnlyPropertyUser")
class User {

    @Id
    String id;

    // I want "username" to be persisted when create an user,
    // and "username" not to be accidentally updated when update an user
    @ReadOnlyProperty
    String username;

    String password;

    public User() { }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
