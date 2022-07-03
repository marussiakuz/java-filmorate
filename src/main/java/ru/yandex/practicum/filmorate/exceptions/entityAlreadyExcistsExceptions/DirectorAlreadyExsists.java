package ru.yandex.practicum.filmorate.exceptions.entityAlreadyExcistsExceptions;

public class DirectorAlreadyExsists extends RuntimeException {

    public DirectorAlreadyExsists(String message) {
        super(message);
    }
}
