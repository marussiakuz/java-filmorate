package ru.yandex.practicum.filmorate.exceptions.entityAlreadyExcistsExceptions;

public class ReviewAlreadyExistsException extends RuntimeException {

    public ReviewAlreadyExistsException(String message) {
        super(message);
    }
}
