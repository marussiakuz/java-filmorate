package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("ratingDbStorage")
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAllMpa() {
        String sqlQuery = "SELECT * FROM rating";

        return jdbcTemplate.query(sqlQuery, this::mapRowToRating);
    }

    @Override
    public Rating getRatingById(int id) {
        String sqlQuery = "SELECT * FROM rating WHERE rating_id = ?";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToRating, id);
    }

    @Override
    public boolean doesRatingExist(int id) {
        String sql = "SELECT COUNT(*) FROM rating WHERE rating_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[] { id }, Integer.class);

        return count > 0;
    }

    private Rating mapRowToRating (ResultSet resultSet, int rowNum) throws SQLException {
        return Rating.builder()
                .id(resultSet.getInt("rating_id"))
                .name(resultSet.getString("name_rating"))
                .build();
    }
}
