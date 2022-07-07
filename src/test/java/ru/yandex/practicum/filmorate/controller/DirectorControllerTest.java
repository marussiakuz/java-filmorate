package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=false"
})
@AutoConfigureTestDatabase
@Sql({"/schema.sql", "/test-data.sql"})
@AutoConfigureMockMvc
class DirectorControllerTest {
    private static Director director;

    @Autowired
    private DirectorController directorController;

    @Qualifier("directorDbStorage")
    @Autowired
    private DirectorStorage directorStorage;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void beforeEach() throws Exception {
        director = Director.builder().id(1).name("Roman Bykov").build();
        mockMvc.perform(post("/directors")
                .contentType("application/json")
                .content(mapper.writeValueAsString(director)));
    }

    @Test
    void addAndGetValidDirectorAndGetAllIsOk() throws Exception {
        Director newDirector = Director.builder().name("Tom Griec").build();

        mockMvc.perform(post("/directors")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(newDirector)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/directors/2"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":2,\"name\":\"Tom Griec\"}"));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/directors"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Roman Bykov"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Tom Griec"));
    }

    @Test
    void updateAndGetValidDirectorisOk() throws Exception {
        director.setName("Roman Polanski");

        mockMvc.perform(put("/directors")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(director)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/directors/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"id\":1,\"name\":\"Roman Polanski\"}"));
    }

    @Test
    void deleteIsOkGetDeletedNotFound() throws Exception {
        mockMvc.perform(delete("/directors/1")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(director)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/directors/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addDirectorWithBlankNameBadRequest() throws Exception {
        mockMvc.perform(put("/directors")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(Director.builder().name(" ").build())))
                .andExpect(status().isBadRequest());
    }
}