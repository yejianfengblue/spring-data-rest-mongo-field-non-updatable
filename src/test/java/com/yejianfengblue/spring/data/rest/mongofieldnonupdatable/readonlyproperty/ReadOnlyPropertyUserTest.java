package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.readonlyproperty;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Start a MongoDB on localhost:27017 before run this test
 */
@SpringBootTest
public class ReadOnlyPropertyUserTest {

    @Autowired
    @Qualifier("readOnlyPropertyUserRepository")
    UserRepository repository;

    @Test
    void givenUsernameAnnotatedReadOnlyProperty_whenCreateUser_thenUsernameIsNotPersisted() {

        User user = new User();
        user.setUsername("a");
        user.setPassword("1");

        User createdUser = repository.save(user);
        // even though the username in the User object returned from repository has the value "a", the username is not actually persisted
        assertThat(createdUser.getUsername()).isEqualTo("a");

        Optional<User> foundUser = repository.findById(createdUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isNull();  // username doesn't exist in DB
        assertThat(foundUser.get().getPassword()).isEqualTo("1");
        /*
        > db.mongoUser.find().pretty()
        {
                "_id" : "27d31882-eaa4-4e48-a97c-68b853021e49",
                "password" : "1",
                "_class" : "com.yejianfengblue.spring.data.readonlyproperty.mongo.user.MongoUser"
        }
         */

    }

}
