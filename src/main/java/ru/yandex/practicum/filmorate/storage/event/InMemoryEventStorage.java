package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import java.util.List;

@Component("inMemoryEventStorage")
public class InMemoryEventStorage implements EventStorage {

    @Override
    public void addAddEvent(int userId, int entityId, EventType eventType) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void addRemoveEvent(int userId, int entityId, EventType eventType) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public void addUpdateEvent(int userId, int entityId, EventType eventType) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    @Override
    public List<Event> getEvents(int userId) {
        throw new UnsupportedOperationException();
    }
}
