package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class IncorrectFilmDataException extends RuntimeException {
    public IncorrectFilmDataException(String message) {
        super(message);
    }
}
