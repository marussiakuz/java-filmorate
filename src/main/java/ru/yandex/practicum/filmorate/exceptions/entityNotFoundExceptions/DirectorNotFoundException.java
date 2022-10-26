package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class DirectorNotFoundException
        extends RuntimeException {
    public DirectorNotFoundException(String message) {
        super(message);
    }
}
