package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        }else throw new DirectorNotFoundException(String.format("Attempt to update the director using " +
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

    public boolean thereIsDirector(int id) {
        String sql = "SELECT * FROM DIRECTOR WHERE director_id = ?";
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sql, id);
        return userRows.next();
    }

    private Director mapRowToDirector(ResultSet resultSet, int rowNum) throws SQLException {
        return Director.builder()
                .id(resultSet.getInt("director_id"))
                .name(resultSet.getString("name_director"))
                .build();
    }

}