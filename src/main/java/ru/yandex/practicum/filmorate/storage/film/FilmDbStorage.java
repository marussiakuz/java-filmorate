package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.LikeNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDbStorage genreDbStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDbStorage = genreDbStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        String sqlQuery = "SELECT * FROM film LEFT JOIN rating USING(rating_id)";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        films.forEach(film -> film.setGenres(genreDbStorage.getGenresByFilmId(film.getId())));
        return films;
    }

    @Override
    public void add(Film film) {
        String sqlQuery = "INSERT INTO film(title, description, release_date, duration, rating_id) SELECT ?, ?, ?, ?, ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration().toMinutes());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        if (film.getGenres() != null) genreDbStorage.addGenresToTheFilm(film);
    }

    @Override
    public void update(Film film) {
        validateFilm(film.getId());

        String sqlQuery = "UPDATE film SET title = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                java.sql.Date.valueOf(film.getReleaseDate()),
                film.getDuration().toMinutes(),
                film.getMpa().getId(),
                film.getId());

        if (film.getGenres() != null) {
            genreDbStorage.deleteGenresByFilmId(film.getId());
            genreDbStorage.addGenresToTheFilm(film);
        }
    }

    @Override
    public Film getFilmById(int id) {
        validateFilm(id);

        String sqlQuery = "SELECT * FROM film INNER JOIN rating USING (rating_id) WHERE film_id = ?";

        Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
        List<Genre> genres = genreDbStorage.getGenresByFilmId(id);
        if (film != null) film.setGenres(genres.isEmpty()? null : genres);

        return film;
    }

    public boolean delete(long id) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateLike(filmId, userId);

        String sqlQuery = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlQuery, userId, filmId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sqlQuery = "SELECT * FROM film LEFT JOIN (SELECT film_id, COUNT(film_id) AS count_like FROM likes " +
                "GROUP BY film_id) USING (film_id) LEFT JOIN rating ON film.rating_id = rating.rating_id " +
                "ORDER BY count_like DESC limit ?";
        List<Film> mostPopularFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        mostPopularFilms.forEach(film -> film.setGenres(genreDbStorage.getGenresByFilmId(film.getId())));
        return mostPopularFilms;
    }

    private Film mapRowToFilm (ResultSet resultSet, int rowNum) throws SQLException {
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

    @Override
    public boolean doesFilmExist(int filmId) {
        String sql = "SELECT COUNT(*) FROM film WHERE film_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { filmId }, Integer.class);
        return count > 0;
    }

    private boolean doesLikeExist(Integer filmId, Integer userId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE user_id = ? AND film_id = ?";
        int count = jdbcTemplate.queryForObject(sql, new Object[] { userId, filmId }, Integer.class);
        return count > 0;
    }

    private void validateLike(int filmId, int userId){
        if (!doesLikeExist(filmId, userId))
            throw new LikeNotFoundException(String.format("Film with id=%s was not liked by a user with id=%s",
                    filmId, userId));
    }
}
