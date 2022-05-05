package ru.yandex.practicum.filmorate.exceptions;

public class InvalidReleaseDateException extends IncorrectFilmDataException {
    public InvalidReleaseDateException(String message) {
        super(message);
    }
}
