package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.expected;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Make username insertable when create, and NOT updatable when update
 */
@Document("expectedUser")
class User {

    static final Logger log = LoggerFactory.getLogger(User.class);

    @Id
    String id;

    // I want "username" to be persisted when create an user,
    // and "username" not to be accidentally updated when update an user
    String username;

    String password;

    @PersistenceConstructor
    User(String id, String username, String password) {

        this.id = id;
        this.username = username;
        this.password = password;
        log.info("PersistenceConstructor is called");
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonSetter
    public void setPassword(String password) {
        this.password = password;
    }
}
