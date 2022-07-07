package ru.yandex.practicum.filmorate.exceptions.entityAlreadyExcistsExceptions;

public class FilmAlreadyExistsException extends RuntimeException {

    public FilmAlreadyExistsException(String message) {
        super(message);
    }
}
