package ru.yandex.practicum.filmorate.model.enums;

public enum EventType {
    LIKE("LIKE"),
    REVIEW("REVIEW"),
    FRIEND("FRIEND");

    private final String eventType;

    private EventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }
}
