package ru.yandex.practicum.filmorate.exceptions;

public class NotImplementedException extends UnsupportedOperationException {

    public NotImplementedException() {
        super("This method is not implement");
    }
}
