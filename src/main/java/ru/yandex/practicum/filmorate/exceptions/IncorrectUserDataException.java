package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Some user data is incorrect")
public class IncorrectUserDataException extends RuntimeException {

    public IncorrectUserDataException(String message) {
        super(message);
    }
}
