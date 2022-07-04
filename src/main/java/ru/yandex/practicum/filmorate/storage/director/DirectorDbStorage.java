package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.MapperToFilm;

import java.sql.PreparedStatement;

import java.util.List;
import java.util.Objects;

@Component("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage, MapperToFilm {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Director director) {
        String sqlQuery = "INSERT INTO director(name_director) VALUES (?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);

        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void delete(int directorId) {
        String sql = "DELETE FROM director WHERE director_id = ?";

        jdbcTemplate.update(sql, directorId);
    }

    @Override
    public void update(Director director) {
        String sqlQuery = "UPDATE director SET name_director = ? WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
    }

    @Override
    public List<Director> getAllDirectors() {
        String sqlQuery = "SELECT * FROM director";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(int id) {
        String sqlQuery = "SELECT * FROM director WHERE director_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
    }

    @Override
    public boolean doesDirectorExist(int directorId) {
        String sql = "SELECT COUNT(*) FROM director WHERE director_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{directorId}, Integer.class);

        return count > 0;
    }

    @Override
    public List<Film> getMostFilmsYear(int directorId) {  // Возвращает список фильмов режиссера отсортированных по году выпуска
        String sq = "SELECT * FROM film AS f LEFT JOIN film_director AS fd ON f.film_id = fd.film_id LEFT JOIN rating r " +
                "ON f.rating_id = R.rating_id WHERE fd.director_id = ? ORDER BY f.release_Date";

        List<Film> yearFilms = jdbcTemplate.query(sq, this::mapRowToFilm, directorId);

        yearFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        yearFilms.stream().map(Film::getGenres).filter(genres -> genres.size() == 0).forEach(genres -> genres = null);

        return yearFilms;
    }

    @Override
    public List<Film> getMostFilmsLikes(int directorId) {  // Возвращает список фильмов режиссера отсортированных по количеству лайков
        String sq = "SELECT * FROM film AS f LEFT JOIN likes AS l ON f.film_id = l.film_id LEFT JOIN film_director " +
                "AS fd ON f.film_id = fd.film_id LEFT JOIN rating r ON f.rating_id = r.rating_id WHERE fd.director_id = ? " +
                "GROUP BY f.film_id ORDER BY COUNT(l.film_id) DESC";

        List<Film> yearFilms = jdbcTemplate.query(sq, new Object[]{directorId}, this::mapRowToFilm);
        yearFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        yearFilms.stream().map(Film::getGenres).filter(genres -> genres.size() == 0).forEach(genres -> genres = null);

        return yearFilms;
    }

    private List<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM genre RIGHT JOIN (SELECT genre_id FROM film_genre WHERE film_id = ?) " +
                "USING(genre_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }
}