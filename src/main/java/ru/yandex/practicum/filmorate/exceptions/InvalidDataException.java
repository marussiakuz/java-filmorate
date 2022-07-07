package ru.yandex.practicum.filmorate.exceptions;

public class InvalidDataException extends RuntimeException {

    public InvalidDataException(String message) {
        super(message);
    }
}
