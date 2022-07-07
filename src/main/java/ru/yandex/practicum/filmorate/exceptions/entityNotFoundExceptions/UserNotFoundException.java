package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
