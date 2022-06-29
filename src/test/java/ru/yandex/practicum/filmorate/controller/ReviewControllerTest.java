package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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
class ReviewControllerTest {

    private static Review review;

    @Autowired
    private ReviewController reviewController;

    @Qualifier("reviewsDbStorage")
    @Autowired
    private ReviewsStorage reviewsStorage;

    @Qualifier("filmDbStorage")
    @Autowired
    private FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    @Autowired
    private UserStorage userStorage;

    @Qualifier("eventDbStorage")
    @Autowired
    private EventStorage eventStorage;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addUpdateGetDelete() throws Exception {
        review = Review.builder()
                .content("very good")
                .userId(1)
                .filmId(1)
                .isPositive(true)
                .build();

        String reviewBody = mapper.writeValueAsString(review);
        this.mockMvc.perform(post("/reviews").content(reviewBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        review.setId(1);
        review.setContent("not recommended");
        review.setPositive(false);

        String updatedReviewBody = mapper.writeValueAsString(review);

        System.out.println("updatedReviewBody " + updatedReviewBody);

        this.mockMvc.perform(put("/reviews").content(updatedReviewBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/reviews/1")).andExpect(status().isOk());

        this.mockMvc.perform(delete("/reviews/1")).andExpect(status().isOk());
    }

    @Test
    void putDeleteLikeAndPutDeleteDislike() throws Exception {
        review = Review.builder()
                .content("very good")
                .userId(1)
                .filmId(1)
                .isPositive(true)
                .build();

        String reviewBody = mapper.writeValueAsString(review);
        this.mockMvc.perform(post("/reviews").content(reviewBody).contentType(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(put("/reviews/1/like/1")).andExpect(status().isOk());

        this.mockMvc.perform(delete("/reviews/1/like/1")).andExpect(status().isOk());

        this.mockMvc.perform(put("/reviews/1/dislike/1")).andExpect(status().isOk());

        this.mockMvc.perform(delete("/reviews/1/dislike/1")).andExpect(status().isOk());
    }

    @Test
    void getReviewsByFilmId() throws Exception {
        review = Review.builder()
                .content("very good")
                .userId(1)
                .filmId(1)
                .isPositive(true)
                .build();

        String reviewBody = mapper.writeValueAsString(review);
        this.mockMvc.perform(post("/reviews").content(reviewBody).contentType(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(get("/reviews?filmId=1&count=5")).andExpect(status().isOk());
    }
}
