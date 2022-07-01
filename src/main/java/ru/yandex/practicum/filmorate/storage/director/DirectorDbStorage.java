package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@Component("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final RatingDbStorage ratingDbStorage;
    private final GenreDbStorage genreDbStorage;


    public DirectorDbStorage(JdbcTemplate jdbcTemplate, RatingDbStorage ratingDbStorage, GenreDbStorage genreDbStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.ratingDbStorage = ratingDbStorage;
        this.genreDbStorage = genreDbStorage;
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
    }

    @Override
    public void delete(int directorId) {
        if (thereIsDirector(directorId)) {
            String sql = "DELETE FROM DIRECTOR WHERE director_id = ?";
            jdbcTemplate.update(sql, directorId);
        } else throw new DirectorNotFoundException(String.format("Attempt to remove the director using " +
                "missing id = %d", directorId));
    }


    @Override
    public void update(Director director) {
        if (thereIsDirector(director.getId())) {
            String sqlQuery = "UPDATE director SET name_director = ?" +
                    "WHERE director_id = ?";

            jdbcTemplate.update(sqlQuery,
                    director.getName(),
                    director.getId());
        } else throw new DirectorNotFoundException(String.format("Attempt to update the director using " +
                "missing id = %d", director.getId()));
    }

    @Override
    public List<Director> getAllDirector() {
        String sqlQuery = "SELECT * FROM director";

        return jdbcTemplate.query(sqlQuery, this::mapRowToDirector);
    }

    @Override
    public Director getDirectorById(int id) {
        if (thereIsDirector(id)) {
            String sqlQuery = "SELECT * FROM director WHERE director_id = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToDirector, id);
        } else throw new DirectorNotFoundException(String.format("Trying to get a director using " +
                "missing id = %d", id));
    }

    @Override
    public boolean doesDirectorExist(int directorId) {
        String sql = "SELECT COUNT(*) FROM DIRECTOR WHERE director_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{directorId}, Integer.class);

        return count > 0;
    }

    public List<Film> getMostFilmsYear(int count) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rate_id " +
                "FROM FILM AS f " +
                "LEFT JOIN FILM_DIRECTOR AS fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.release_Date;";
        List<Film> yearFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        yearFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        return yearFilms;
    }


    public  List<Film> getMostFilmsLiks(int count) {
        String sqlQuery = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.rate_id \" +\n" +
                "                \"FROM FILM AS f \" +\n" +
                "                \"LEFT JOIN LIKES AS l ON f.film_id = l.film_id \" +\n" +
                "                \"LEFT JOIN FILM_DIRECTOR AS fd ON f.film_id = fd.film_id \" +\n" +
                "                \"WHERE fd.director_id = ? \" +\n" +
                "                \"GROUP BY f.FILM_ID \" +\n" +
                "                \"ORDER BY COUNT(l.FILM_ID) DESC;";
        List<Film> liksFilms = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
        liksFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        return liksFilms;
    }

    private List<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM genre RIGHT JOIN (SELECT genre_id FROM film_genre WHERE film_id = ?) " +
                "USING(genre_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
    }
    public boolean thereIsDirector(int id) {
        String sql = "SELECT * FROM DIRECTOR WHERE director_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }
    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
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

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("name_director"))
                .build();
    }
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name_genre"))
                .build();
    }
    public  boolean isDirectorExists(Integer id) {
        String sql = "SELECT director_name FROM directors WHERE director_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }
}