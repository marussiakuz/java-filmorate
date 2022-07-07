package ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions;

public class FriendNotFoundException extends RuntimeException {

    public FriendNotFoundException(String message) {
        super(message);
    }
}
