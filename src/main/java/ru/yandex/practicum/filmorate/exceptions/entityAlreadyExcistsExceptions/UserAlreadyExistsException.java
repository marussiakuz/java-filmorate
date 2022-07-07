package ru.yandex.practicum.filmorate.exceptions.entityAlreadyExcistsExceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
