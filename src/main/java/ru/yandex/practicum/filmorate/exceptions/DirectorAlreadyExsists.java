package ru.yandex.practicum.filmorate.exceptions;

public class DirectorAlreadyExsists extends RuntimeException {

    public DirectorAlreadyExsists(String message) {
        super(message);
    }
}
