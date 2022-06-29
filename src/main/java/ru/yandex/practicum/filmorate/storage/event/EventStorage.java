package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.util.List;

public interface EventStorage {

    public void addAddEvent(int userId, int entityId, EventType eventType);
    public void addRemoveEvent(int userId, int entityId, EventType eventType);
    public void addUpdateEvent(int userId, int entityId, EventType eventType);
    public List<Event> getEvents(int userId);
}
