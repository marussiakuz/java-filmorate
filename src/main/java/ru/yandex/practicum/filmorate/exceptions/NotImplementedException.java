package ru.yandex.practicum.filmorate.exceptions;

public class NotImplementedException extends RuntimeException {

    public NotImplementedException() {
        super("This method is not implement");
    }
}
