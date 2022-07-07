package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
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

        return count <= 0;
    }

    @Override
    public List<Director> fillDirector(int filmId) {  // получить список режиссеров по id фильма
        String sqlQuery = "SELECT * FROM director RIGHT JOIN (SELECT director_id FROM film_director WHERE film_id = ?) " +
                "USING(director_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector, filmId);
    }

    @Override
    public void addDirectorToTheFilm(Film film) {
        if (film.getDirectors() == null || film.getDirectors().isEmpty())
            return;

        String sqlQuery = "INSERT INTO film_director(film_id, director_id) SELECT ?, ?";

        film.getDirectors().stream()
                .mapToInt(Director::getId)
                .distinct()
                .forEach(directorId -> jdbcTemplate.update(sqlQuery, film.getId(), directorId));

        film.setDirectors(fillDirector(film.getId()));
    }

    @Override
    public void deleteDirectorsByFilmId(int filmId) {
        String sqlQuery = "DELETE FROM FILM_DIRECTOR WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, filmId);
    }
}