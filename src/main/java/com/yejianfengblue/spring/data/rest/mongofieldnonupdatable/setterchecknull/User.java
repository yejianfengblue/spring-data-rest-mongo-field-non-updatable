package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.setterchecknull;

import com.fasterxml.jackson.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Make username insertable when create, and NOT updatable when update via PATCH
 */
@Document("setterCheckNullUser")
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

    @JsonCreator
    public User(@JsonProperty("username") String username,
                @JsonProperty("password") String password) {

        this.username = username;
        this.password = password;
        log.info("JsonCreator is called");
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonGetter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (null == this.username)
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
