package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sqlQuery = "SELECT * FROM users";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public void add(User user) {
        String sqlQuery = "INSERT INTO users(name, email, login, birthday) VALUES (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"user_id"});
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getLogin());
            stmt.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
    }

    @Override
    public void update(User user) {
        String sqlQuery = "UPDATE users SET name = ?, email = ?, login = ?, birthday = ? WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery,
                user.getName() == null || user.getName().isBlank() ? user.getLogin() : user.getName(),
                user.getEmail(),
                user.getLogin(),
                java.sql.Date.valueOf(user.getBirthday()),
                user.getId());
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";

        return Optional.ofNullable(jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id));
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id, friend_id, is_confirmed) VALUES (?, ?, 0)";

        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public List<User> getAllFriends(int userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friendship WHERE user_id = ?)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId);
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friendship WHERE user_id = ? " +
                "OR user_id = ? GROUP BY friend_id HAVING count(friend_id) = 2)";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, otherUserId);
    }

    @Override
    public boolean doesUserExist(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{userId}, Integer.class);

        return count > 0;
    }

    @Override
    public boolean doesFriendExist(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";

        int count = jdbcTemplate.queryForObject(sql, new Object[]{userId, friendId}, Integer.class);

        return count > 0;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}
