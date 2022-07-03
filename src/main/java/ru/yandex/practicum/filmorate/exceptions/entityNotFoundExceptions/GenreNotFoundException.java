package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class GenreNotFoundException extends RuntimeException {

    public GenreNotFoundException(String message) {
        super(message);
    }
}
