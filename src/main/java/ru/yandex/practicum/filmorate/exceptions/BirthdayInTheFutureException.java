package ru.yandex.practicum.filmorate.exceptions;

public class BirthdayInTheFutureException extends IncorrectUserDataException {
    public BirthdayInTheFutureException(String message) {
        super(message);
    }
}
