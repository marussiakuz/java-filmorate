package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
