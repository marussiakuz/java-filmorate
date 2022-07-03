package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class RatingNotFoundException extends RuntimeException {

    public RatingNotFoundException(String message) {
        super(message);
    }
}
