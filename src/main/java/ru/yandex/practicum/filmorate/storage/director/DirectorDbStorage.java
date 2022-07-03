package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Component("directorDbStorage")
public class DirectorDbStorage implements DirectorStorage {
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
        String sql = "DELETE FROM DIRECTOR WHERE director_id = ?";

        jdbcTemplate.update(sql, directorId);
    }

    @Override
    public void update(Director director) {
        String sqlQuery = "UPDATE director SET name_director = ? WHERE director_id = ?";

        jdbcTemplate.update(sqlQuery, director.getName(), director.getId());
    }

    @Override
    public List<Director> getAllDirector() {
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
        String sql = "SELECT COUNT(*) FROM DIRECTOR WHERE director_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{directorId}, Integer.class);

        return count > 0;
    }

    public List<Film> getMostFilmsYear(int directorId) {
        String sq = "SELECT * FROM FILM AS f LEFT JOIN FILM_DIRECTOR AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN RATING R ON f.RATING_ID = R.RATING_ID WHERE fd.director_id = ? ORDER BY f.release_Date";

        List<Film> yearFilms = jdbcTemplate.query(sq, this::mapRowToFilm, directorId);
        yearFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));
        yearFilms.stream().map(Film::getGenres).filter(genres -> genres.size() == 0).forEach(genres -> genres = null);

        return yearFilms;
    }

    public List<Film> getMostFilmsLikes(int directorId) {
        String sq = "SELECT * FROM FILM RIGHT JOIN (SELECT * FROM film_director) USING (film_id) INNER JOIN " +
                "(SELECT film_id, COUNT(*) AS count_of_likes FROM likes GROUP BY film_id) USING (film_id) " +
                "WHERE director_id = ? ORDER BY count_of_likes DESC";

        List<Film> likesFilms = jdbcTemplate.query(sq, this::mapRowToFilm, directorId);
        likesFilms.forEach(film -> film.setGenres(getGenresByFilmId(film.getId())));

        return likesFilms;
    }

    private List<Genre> getGenresByFilmId(int filmId) {
        String sqlQuery = "SELECT * FROM genre RIGHT JOIN (SELECT genre_id FROM film_genre WHERE film_id = ?) " +
                "USING(genre_id)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToGenre, filmId);
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

    public boolean isDirectorExists(Integer id) {
        String sql = "SELECT name_director FROM director WHERE director_id = ?";

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }
}