package com.pukhovkirill.mapsketch.controller;

import java.util.List;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.pukhovkirill.mapsketch.entity.GeoObject;
import com.pukhovkirill.mapsketch.repository.GeoObjectRepository;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.pukhovkirill.mapsketch.MapSketchApplication.class
)
@Testcontainers
@AutoConfigureMockMvc
class GeoObjectRestControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GeoObjectRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.findAll()
                .stream()
                .map(GeoObject::id)
                .forEach(repository::deleteById);
    }

    @Test
    void findAll_returnsEmptyFeatureCollectionWhenNoObjectsExist() throws Exception {
        mockMvc.perform(get("/api/v1/geoobjects/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features").isArray())
                .andExpect(jsonPath("$.features").isEmpty());
    }

    @Test
    void saveAll_persistsNewObjects_and_findAllReturnsThem() throws Exception {
        String requestBody = """
            {
              "type": "FeatureCollection",
              "features": [
                {
                  "type": "Feature",
                  "geometry": {
                    "type": "Point",
                    "coordinates": [30.0, 50.0]
                  },
                  "properties": {
                    "isNewFeature": true,
                    "name": "test1",
                    "tempId": "tempId23142346123412351324"
                  }
                }
              ]
            }
            """;

        // POST /api/v1/geoobjects
        mockMvc.perform(post("/api/v1/geoobjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("FeatureCollection"))
                .andExpect(jsonPath("$.features[0].properties.id").isNumber());

        // GET /api/v1/geoobjects/list should now return one feature
        mockMvc.perform(get("/api/v1/geoobjects/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.features").isArray())
                .andExpect(jsonPath("$.features.length()").value(1));
    }

    @Test
    void rename_updatesNameOfExistingGeoObject() throws Exception {
        GeoObject original = new GeoObject()
                .name("test")
                .type(GeoObject.GeoObjectType.POINT)
                .coordinates("[55.123456654321123,44.654321123123456]");
        repository.saveAll(List.of(original));
        Long id = repository.findAll().getFirst().id();

        // PUT /api/v1/geoobjects/{id}
        String newName = "beta";
        mockMvc.perform(put("/api/v1/geoobjects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newName))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));

        GeoObject updated = repository.findById(id);
        assertEquals(newName, updated.name());
    }

    @Test
    void delete_removesExistingGeoObject() throws Exception {
        GeoObject toDelete = new GeoObject()
                .name("test")
                .type(GeoObject.GeoObjectType.POINT)
                .coordinates("[55.0,44.0]");
        repository.saveAll(List.of(toDelete));
        Long id = repository.findAll().getFirst().id();

        // DELETE /api/v1/geoobjects/{id} returns the deleted id
        mockMvc.perform(delete("/api/v1/geoobjects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(id.toString()));

        assertNull(repository.findById(id));
    }

    @Test
    void rename_nonExistingGeoObject_returnsBadRequest() throws Exception {
        long nonExistingId = 999L;
        String name = "beta";
        mockMvc.perform(put("/api/v1/geoobjects/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(name))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad request"))
                .andExpect(jsonPath("$.message")
                        .value("Object with id " + nonExistingId + " not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void rename_withInvalidIdType_returnsBadRequest() throws Exception {
        String name = "beta";
        mockMvc.perform(put("/api/v1/geoobjects/{id}", "abc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(name))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Bad request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    void delete_nonExistingGeoObject_returnsIdWithoutError() throws Exception {
        long nonExistingId = 1234L;
        mockMvc.perform(delete("/api/v1/geoobjects/{id}", nonExistingId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(nonExistingId)));
    }
}
