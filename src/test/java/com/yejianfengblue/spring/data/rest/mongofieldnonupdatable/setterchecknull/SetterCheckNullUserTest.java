package com.yejianfengblue.spring.data.rest.mongofieldnonupdatable.setterchecknull;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.data.rest.webmvc.RestMediaTypes;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 *
 *
 * Start a MongoDB on localhost:27017 before run this test
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SetterCheckNullUserTest {

    static final String REST_COLLECTION_PATH = "/setterCheckNullUsers";

    @Autowired
    @Qualifier("setterCheckNullUserRepository")
    UserRepository repository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @TestConfiguration
    static class RepositoryRestConfig implements RepositoryRestConfigurer {

        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration repositoryRestConfiguration) {
            ExposureConfiguration exposureConfiguration = repositoryRestConfiguration.getExposureConfiguration();
            exposureConfiguration.disablePutForCreation();
            exposureConfiguration.disablePutOnItemResources();
        }
    }

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
    void whenPutRequest_thenMethodNotAllowed() throws Exception {

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
                .andExpect(status().isMethodNotAllowed());
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

        // PATCH update
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

        assertThat(updatedUser.getUsername()).isEqualTo("a");
        assertThat(updatedUser.getPassword()).isEqualTo("2");
    }
}