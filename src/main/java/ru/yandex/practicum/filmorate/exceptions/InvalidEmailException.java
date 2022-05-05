package ru.yandex.practicum.filmorate.exceptions;

public class InvalidEmailException extends IncorrectUserDataException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
