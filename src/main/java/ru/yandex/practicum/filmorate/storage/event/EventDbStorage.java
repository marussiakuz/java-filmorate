package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("eventDbStorage")
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addAddEvent(int userId, int entityId, EventType eventType) {  // добавить событие ADD
        String sqlQuery = "INSERT INTO events(user_id, entity_id, event_type, operation, time_stamp) " +
                "SELECT ?, ?, ?, ?, ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            stmt.setInt(1, userId);
            stmt.setInt(2, entityId);
            stmt.setString(3, eventType.getEventType());
            stmt.setString(4, "ADD");
            stmt.setLong(5, System.currentTimeMillis());
            return stmt;
        }, new GeneratedKeyHolder());
    }

    @Override
    public void addRemoveEvent(int userId, int entityId, EventType eventType) {  //  добавить событие REMOVE
        String sqlQuery = "INSERT INTO events(user_id, entity_id, event_type, operation, time_stamp) " +
                "SELECT ?, ?, ?, ?, ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            stmt.setInt(1, userId);
            stmt.setInt(2, entityId);
            stmt.setString(3, eventType.getEventType());
            stmt.setString(4, "REMOVE");
            stmt.setLong(5, System.currentTimeMillis());
            return stmt;
        }, new GeneratedKeyHolder());
    }

    @Override
    public void addUpdateEvent(int userId, int entityId, EventType eventType) {  //  добавить событие UPDATE
        String sqlQuery = "INSERT INTO events(user_id, entity_id, event_type, operation, time_stamp) " +
                "SELECT ?, ?, ?, ?, ?";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            stmt.setInt(1, userId);
            stmt.setInt(2, entityId);
            stmt.setString(3, eventType.getEventType());
            stmt.setString(4, "UPDATE");
            stmt.setLong(5, System.currentTimeMillis());
            return stmt;
        }, new GeneratedKeyHolder());
    }

    @Override
    public List<Event> getEvents(int userId) {  // возвращает список событий определенного пользователя
        String sqlQuery = "SELECT * FROM events WHERE user_id = ?";

        return jdbcTemplate.query(sqlQuery, this::mapRowToEvent, userId);
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        int eventId = resultSet.getInt("event_id");
        return Event.builder()
                .id(eventId)
                .userId(resultSet.getInt("user_id"))
                .entityId(resultSet.getInt("entity_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(Operation.valueOf(resultSet.getString("operation")))
                .timestamp(resultSet.getLong("time_stamp"))
                .build();
    }
}
