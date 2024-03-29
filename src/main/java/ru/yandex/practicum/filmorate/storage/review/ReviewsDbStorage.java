package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("reviewsDbStorage")
public class ReviewsDbStorage implements ReviewsStorage {  // класс для обращения к БД отзывов
    private final JdbcTemplate jdbcTemplate;

    public ReviewsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Review review) {
        String sqlQuery = "INSERT INTO review(review_content, is_positive, user_id, film_id) SELECT ?, ?, ?, ?";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.isPositive());
            stmt.setInt(3, review.getUserId());
            stmt.setInt(4, review.getFilmId());
            return stmt;
        }, keyHolder);

        review.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(Review review) {
        String sqlQuery = "UPDATE review SET review_content = ?, is_positive = ? WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery,
                review.getContent(),
                review.isPositive(),
                review.getId());
    }

    @Override
    public void delete(int id) {
        String sqlQuery = "DELETE FROM review WHERE review_id = ?";

        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        String sqlQuery = "SELECT * FROM review LEFT JOIN (SELECT review_id, SUM(like_dislike) AS usefulness " +
                "FROM review_usefulness GROUP BY review_id) USING (review_id) WHERE review.review_id = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToReview, id));
    }

    @Override
    public List<Review> getAllReviews(int count) {
        String sqlQuery = "SELECT *, COALESCE(count_useful, 0) AS usefulness FROM review LEFT JOIN (SELECT review_id, " +
                "SUM(like_dislike) AS count_useful FROM review_usefulness GROUP BY review_id) AS useful USING (review_id) " +
                "ORDER BY usefulness DESC limit ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
    }

    @Override
    public List<Review> getReviewsByFilmId(int id, int count) {  // получить список отзывов по конкретному фильму
        String sqlQuery = "SELECT *, COALESCE(count_useful, 0) AS usefulness FROM review LEFT JOIN (SELECT review_id, " +
                "SUM(like_dislike) AS count_useful FROM review_usefulness GROUP BY review_id) AS useful USING (review_id) " +
                "WHERE film_id = ? ORDER BY usefulness DESC limit ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToReview, id, count);
    }

    @Override
    public void putLike(int reviewId, int userId) {  // добавить отзыву +1 (лайк)
        String sqlQuery = "INSERT INTO review_usefulness (review_id, user_id, like_dislike) VALUES (?, ?, 1)";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void putDislike(int reviewId, int userId) {  // добавить отзыву -1 (дизлайк)
        String sqlQuery = "INSERT INTO review_usefulness (review_id, user_id, like_dislike) VALUES (?, ?, -1)";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {  // удалить лайк
        String sqlQuery = "DELETE FROM review_usefulness WHERE review_id = ? AND user_id = ? AND like_dislike = 1";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {  // удалить дизлайк
        String sqlQuery = "DELETE FROM review_usefulness WHERE review_id = ? AND user_id = ? AND like_dislike = -1";

        jdbcTemplate.update(sqlQuery, reviewId, userId);
    }

    @Override
    public boolean doesLikeExist(int reviewId, int userId) {
        String sql = "SELECT COUNT(*) FROM review_usefulness WHERE review_id = ? AND user_id = ? AND like_dislike = 1";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{reviewId, userId}, Integer.class);

        return count > 0;
    }

    @Override
    public boolean doesDislikeExist(int reviewId, int userId) {
        String sql = "SELECT COUNT(*) FROM review_usefulness WHERE review_id = ? AND user_id = ? AND like_dislike = -1";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{reviewId, userId}, Integer.class);

        return count > 0;
    }

    @Override
    public boolean doesReviewExist(int id) {
        String sql = "SELECT COUNT(*) FROM review WHERE review_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{id}, Integer.class);

        return count > 0;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        int reviewId = resultSet.getInt("review_id");
        return Review.builder()
                .id(reviewId)
                .content(resultSet.getString("review_content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .userId(resultSet.getInt("user_id"))
                .filmId(resultSet.getInt("film_id"))
                .useful(resultSet.getInt("usefulness"))
                .build();
    }
}
