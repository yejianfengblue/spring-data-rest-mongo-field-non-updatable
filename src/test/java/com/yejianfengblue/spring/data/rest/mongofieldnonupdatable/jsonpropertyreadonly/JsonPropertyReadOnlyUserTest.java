package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.jsonpropertyreadonly;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@code @JsonProperty(access = ReadOnly)} makes username un-insertable when create via POST request.
 *
 * Start a MongoDB on localhost:27017 before run this test
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JsonPropertyReadOnlyUserTest {

    static final String REST_COLLECTION_PATH = "/jsonPropertyReadOnlyUsers";

    @Autowired
    @Qualifier("jsonPropertyReadOnlyUserRepository")
    UserRepository repository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    Logger log = LoggerFactory.getLogger(getClass());

    @Test
    void whenCreateUserViaRepository_thenUsernameIsPersisted() {

        User user = new User(null, "a", "1");

        User createdUser = repository.save(user);

        Optional<User> foundUser = repository.findById(createdUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("a");
        assertThat(foundUser.get().getPassword()).isEqualTo("1");
    }

    @Test
    void givenJsonPropertyAccessReadOnly_whenCreateUser_thenUsernameIsUninsertable() throws Exception {

        // POST create with username "a"
        String userLocation = this.mockMvc.perform(
                post(REST_COLLECTION_PATH)
                        .contentType(RestMediaTypes.HAL_JSON)
                        .content("{ \"username\" : \"a\", \"password\" : \"1\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse()
                .getHeader(HttpHeaders.LOCATION);
        log.info("POST done");

        // username
        this.mockMvc.perform(
                get(userLocation).accept(RestMediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                // @JsonProperty(access=READ_ONLY) make username un-insertable when create
                .andExpect(jsonPath("username").doesNotExist())
                .andExpect(jsonPath("password").doesNotExist());
        log.info("GET done");
    }

    @Test
    void givenJsonPropertyAccessReadOnly_whenDeserializeUser_thenUsernameIsIgnored() throws JsonProcessingException {

        User user = objectMapper.readerFor(User.class).
                readValue("{ \"username\" : \"a\", \"password\" : \"1\" }");

        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isEqualTo("1");
    }

}
