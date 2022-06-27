package ru.yandex.practicum.filmorate.exceptions;

public class LikeNotFoundException extends RuntimeException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}
