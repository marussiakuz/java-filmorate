package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(String message) {
        super(message);
    }
}
