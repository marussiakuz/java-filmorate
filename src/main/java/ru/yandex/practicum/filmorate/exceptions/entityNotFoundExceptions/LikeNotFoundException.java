package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class LikeNotFoundException extends RuntimeException {

    public LikeNotFoundException(String message) {
        super(message);
    }
}
