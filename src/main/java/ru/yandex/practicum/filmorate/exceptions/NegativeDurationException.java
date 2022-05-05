package ru.yandex.practicum.filmorate.exceptions;

public class NegativeDurationException extends IncorrectFilmDataException {
    public NegativeDurationException(String message) {
        super(message);
    }
}
