package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

public interface MapperToFilm {

    default Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        int filmId = resultSet.getInt("film_id");
        return Film.builder()
                .id(filmId)
                .name(resultSet.getString("title"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(Duration.ofMinutes(resultSet.getLong("duration")))
                .mpa(Rating.builder()
                        .id(resultSet.getInt("rating_id"))
                        .name(resultSet.getString("name_rating"))
                        .build())
                .build();
    }

    default Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name_genre"))
                .build();
    }

    default Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("name_director"))
                .build();
    }
}
