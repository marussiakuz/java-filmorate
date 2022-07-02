package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.util.List;

public interface EventStorage {

    void addAddEvent(int userId, int entityId, EventType eventType);

    void addRemoveEvent(int userId, int entityId, EventType eventType);

    void addUpdateEvent(int userId, int entityId, EventType eventType);

    List<Event> getEvents(int userId);
}
