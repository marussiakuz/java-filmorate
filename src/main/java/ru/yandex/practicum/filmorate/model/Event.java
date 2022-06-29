package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@Builder
public class Event {

    private int id;
    private int userId;
    private int entityId;
    private EventType eventType;
    private Operation operation;
    private long timestamp;

    @JsonProperty("eventId")
    public int getId() {
        return id;
    }
}
