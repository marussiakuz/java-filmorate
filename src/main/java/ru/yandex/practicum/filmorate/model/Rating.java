package ru.yandex.practicum.filmorate.model;

public enum Rating {
    G("G"),
    PG("PG"),
    PG13("PG-13"),
    R("R"),
    NC17("NC-17");

    private final String type;

    private Rating(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
