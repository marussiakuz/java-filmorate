package ru.yandex.practicum.filmorate.exceptions;

public class InvalidLoginException extends IncorrectUserDataException {
    public InvalidLoginException(String message) {
        super(message);
    }
}
