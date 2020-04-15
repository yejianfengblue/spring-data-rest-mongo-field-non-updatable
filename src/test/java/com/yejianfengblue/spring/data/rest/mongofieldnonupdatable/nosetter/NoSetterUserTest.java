package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.nosetter;


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
 * If the "username" is contained in PUT or PATCH request body with a different value, username get updated,
 * which is not expected.
 *
 * Start a MongoDB on localhost:27017 before run this test
 */
@SpringBootTest
@AutoConfigureMockMvc
public class NoSetterUserTest {

    static final String REST_COLLECTION_PATH = "/noSetterUsers";

    @Autowired
    @Qualifier("noSetterUserRepository")
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
    void whenPutRequestBodyContainUsername_thenUsernameIsNotUpdated() throws Exception {

        // POST create with username "a"
        String userLocation = this.mockMvc.perform(
                post(REST_COLLECTION_PATH)
                        .contentType(RestMediaTypes.HAL_JSON)
                        .content("{ \"username\" : \"a\", \"password\" : \"1\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse()
                .getHeader(HttpHeaders.LOCATION);

        // PUT update, someone illegally include property username with another value
        this.mockMvc.perform(
                put(userLocation)
                        .contentType(RestMediaTypes.HAL_JSON)
                        .content("{ \"username\" : \"b\", \"password\" : \"2\" }"))
                .andExpect(status().is2xxSuccessful());

        // username
        this.mockMvc.perform(
                get(userLocation).accept(RestMediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                // I want username to remain "a", not "b"
                .andExpect(jsonPath("username").value("a"))
                .andExpect(jsonPath("password").doesNotExist());
    }


    @Test
    void whenPatchRequestBodyContainUsername_thenUsernameIsNotUpdated() throws Exception {

        // POST create
        String userLocation = this.mockMvc.perform(
                post(REST_COLLECTION_PATH)
                        .contentType(RestMediaTypes.HAL_JSON)
                        .content("{ \"username\" : \"a\", \"password\" : \"1\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse()
                .getHeader(HttpHeaders.LOCATION);

        // PATCH update, someone illegally include property username with another value
        this.mockMvc.perform(
                patch(userLocation)
                        .contentType(RestMediaTypes.MERGE_PATCH_JSON)
                        .content("{ \"username\" : \"b\", \"password\" : \"2\" }"))
                .andExpect(status().is2xxSuccessful());

        // GET verify
        this.mockMvc.perform(
                get(userLocation).accept(RestMediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                // I want username to remain "a", not "b"
                .andExpect(jsonPath("username").value("a"))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    void whenPatchRequestBodyNotContainUsername_thenUsernameNotUpdated() throws Exception {

        // POST create
        String userLocation = this.mockMvc.perform(
                post(REST_COLLECTION_PATH)
                        .contentType(RestMediaTypes.HAL_JSON)
                        .content("{ \"username\" : \"a\", \"password\" : \"1\" }"))
                .andExpect(status().isCreated())
                .andReturn().getResponse()
                .getHeader(HttpHeaders.LOCATION);

        // PATCH update, someone illegally include property username with another value
        this.mockMvc.perform(
                patch(userLocation)
                        .contentType(RestMediaTypes.MERGE_PATCH_JSON)
                        .content("{ \"password\" : \"2\" }"))
                .andExpect(status().is2xxSuccessful());

        // GET verify
        this.mockMvc.perform(
                get(userLocation).accept(RestMediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("a"))
                .andExpect(jsonPath("password").doesNotExist());
    }

    @Test
    void givenJsonContainingUsername_whenUseObjectMapperReaderForUpdating_thenUsernameIsNotUpdated() throws JsonProcessingException {

        User user = new User(null, "a", "1");

        User updatedUser = objectMapper.readerForUpdating(user).
                readValue("{ \"username\" : \"b\", \"password\" : \"2\" }");

        // I want username to remain "a", not "b"
        assertThat(updatedUser.getUsername()).isEqualTo("a");
        assertThat(updatedUser.getPassword()).isEqualTo("2");
    }
}