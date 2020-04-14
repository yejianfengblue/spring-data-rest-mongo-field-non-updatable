package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.transient_;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Field username is annotated with {@link Transient}
 */
@Document("transientUser")
class User {

    @Id
    String id;

    // I want "username" to be persisted when create an user,
    // and "username" not to be accidentally updated when update an user
    @Transient
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
