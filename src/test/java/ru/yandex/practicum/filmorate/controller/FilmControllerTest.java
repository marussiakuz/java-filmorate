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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureTestDatabase
@Sql({"/schema.sql", "/test-data.sql"})
@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.liquibase.enabled=false",
        "spring.flyway.enabled=false"
})

@AutoConfigureMockMvc
class FilmControllerTest {

    private static Film film;

    @Autowired
    private FilmController filmController;

    @Qualifier("filmDbStorage")
    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    private void beforeEach() {
        film = new Film();
        film.setId(6);
        film.setName("Film");
        film.setDescription("Comedy");
        film.setDuration(Duration.ofMinutes(130));
        film.setReleaseDate(LocalDate.of(2012, Month.DECEMBER, 12));
        film.setMpa(Rating.builder().id(1).name("G").build());
        film.setGenres(List.of(Genre.builder().id(1).name("Комедия").build()));
    }

    @Test
    void addValidFilmIsOk() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
    }

    @Test
    void updateValidFilmIsOk() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setDescription("thriller");
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        Optional<Film> optionalFilm = filmStorage.getFilmById(6);

        assertTrue(optionalFilm.isPresent());

        Film updatedFilm = optionalFilm.get();

        assertThat(updatedFilm.getDescription()).isEqualTo("thriller");
    }

    @Test
    void addFilmWithInvalidName() throws Exception {
        film.setName("");
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithInvalidName() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setName("");
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithTooLongDescription() throws Exception {
        String description = "x".repeat(201);
        film.setDescription(description);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithInvalidReleaseDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmWithNegativeDuration() throws Exception {
        film.setDuration(Duration.ofMinutes(-120));

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithTooLongDescription() throws Exception {
        film.setDescription("x".repeat(201));
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithInvalidReleaseDate() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommonFilms() throws Exception {
        String user1 = "{\"login\": \"dolore\", \"name\": \"Nick Name\", \"email\": \"mail@mail.ru\", " +
                "\"birthday\": \"1946-08-20\"}";
        String user2 = "{\"login\": \"dolores\", \"name\": \"Nick Names\", \"email\": \"mail@gmail.ru\", " +
                "\"birthday\": \"1946-08-20\"}";
        String user3 = "{\"login\": \"dolorez\", \"name\": \"Nick Namez\", \"email\": \"mail@imail.ru\", " +
                "\"birthday\": \"1946-08-20\"}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
        film.setId(7);
        film.setName("NewFilm");
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(user1))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(user2))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(user3))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films/1/like/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films/2/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films/2/like/2"))
                .andExpect(status().isOk());
        mockMvc.perform(put("/films/2/like/3"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/common?userId=1&friendId=2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/common?userId=55&friendId=2"))
                .andExpect(status().isNotFound());
        assertEquals(2, filmStorage.getCommonFilms(1, 2).size());
        assertEquals(2, filmStorage.getCommonFilms(1, 2).get(0).getId());
        assertEquals(1, filmStorage.getCommonFilms(1, 2).get(1).getId());
    }

    @Test
    void getRecommendations() throws Exception {

        String user1 = "{\"login\": \"dolore\", \"name\": \"Nick Name\", \"email\": \"mail@mail.ru\", " +
                "\"birthday\": \"1946-08-20\"}";
        String user2 = "{\"login\": \"dolores\", \"name\": \"Nick Names\", \"email\": \"mail@gmail.ru\", " +
                "\"birthday\": \"1946-08-20\"}";
        String user3 = "{\"login\": \"dolorez\", \"name\": \"Nick Namez\", \"email\": \"mail@imail.ru\", " +
                "\"birthday\": \"1946-08-20\"}";

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
        film.setId(7);
        film.setName("NewFilm");
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(user1))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(user2))
                .andExpect(status().isOk());
        mockMvc.perform(get("/users/6/recommendations"))
                .andExpect(status().isOk());

//        mockMvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content(user3))
//                .andExpect(status().isOk());
//        mockMvc.perform(put("/films/1/like/1"))
//                .andExpect(status().isOk());
//        mockMvc.perform(put("/films/1/like/2"))
//                .andExpect(status().isOk());
//        mockMvc.perform(put("/films/2/like/1"))
//                .andExpect(status().isOk());
//        mockMvc.perform(put("/films/2/like/2"))
//                .andExpect(status().isOk());
//        mockMvc.perform(put("/films/2/like/3"))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/films/common?userId=1&friendId=2"))
//                .andExpect(status().isOk());
//        mockMvc.perform(get("/films/common?userId=55&friendId=2"))
//                .andExpect(status().isNotFound());
//        assertEquals(2, filmStorage.getCommonFilms(1, 2).size());
//        assertEquals(2, filmStorage.getCommonFilms(1, 2).get(0).getId());
//        assertEquals(1, filmStorage.getCommonFilms(1, 2).get(1).getId());
    }
}