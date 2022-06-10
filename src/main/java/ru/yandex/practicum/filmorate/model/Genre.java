package ru.yandex.practicum.filmorate.model;

public enum Genre {
    COMEDY("COMEDY"),
    DRAMA("DRAMA"),
    CARTOON("CARTOON"),
    THRILLER("THRILLER"),
    DOCUMENTARY("DOCUMENTARY"),
    ACTION("ACTION");

    private final String type;

    private Genre(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
