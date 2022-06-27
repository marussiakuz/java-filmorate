package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        film.setName("Film");
        film.setDescription("Comedy");
        film.setDuration(Duration.ofMinutes(130));
        film.setReleaseDate(LocalDate.of(2012, Month.DECEMBER, 12));
    }

    @Test
    void addValidFilmIsOk() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
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
    void updateValidFilmIsOk() throws Exception {
        film.setId(60);
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setDescription("thriller");
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        Optional<Film> optionalFilm = filmStorage.getFilmById(60);

        assertTrue(optionalFilm.isPresent());

        Film updatedFilm = optionalFilm.get();

        assertThat(updatedFilm.getDescription()).isEqualTo("thriller");
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
    void updateFilmWithTooLongDescription() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setDescription("x".repeat(201));
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmWithInvalidReleaseDate() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());

        film.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));
        mockMvc.perform(put("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommonFilms() throws Exception {
        User user1 = new User();
        user1.setName("User1");
        user1.setLogin("User1");
        user1.setEmail("email@mail.ru");
        user1.setBirthday(LocalDate.of(1990, Month.NOVEMBER, 17));
        User user2 = new User();
        user2.setName("User2");
        user2.setLogin("User2");
        user2.setEmail("email@gmail.ru");
        user2.setBirthday(LocalDate.of(1991, Month.NOVEMBER, 17));
        User user3 = new User();
        user3.setName("User3");
        user3.setLogin("User3");
        user3.setEmail("email@ymail.ru");
        user3.setBirthday(LocalDate.of(1992, Month.NOVEMBER, 17));
        Rating rating = Rating.builder()
                .id(1)
                .name("G")
                .build();
        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Comedy");
        film2.setDuration(Duration.ofMinutes(150));
        film2.setReleaseDate(LocalDate.of(2013, Month.DECEMBER, 12));
        film2.setMpa(rating);
        film.setMpa(rating);

        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/films")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(film2)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user1)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user2)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(mapper.writeValueAsString(user3)))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/1/like/2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/2/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/2/like/2"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/2/like/3"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/films/common?userId=1&friendId=2"))
                .andExpect(status().isOk());
         filmStorage.getCommonFilms(1,2);
        assertEquals(2,filmStorage.getCommonFilms(1,2).size());
        assertEquals(2,filmStorage.getCommonFilms(1,2).get(0).getId());
        assertEquals(1,filmStorage.getCommonFilms(1,2).get(1).getId());
    }
}