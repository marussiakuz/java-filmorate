package ru.yandex.practicum.filmorate.controller.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.entityIsNullExceptions.FilmIsNullException;
import ru.yandex.practicum.filmorate.exceptions.entityIsNullExceptions.UserIsNullException;
import ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions.*;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException e) {
        Optional<FieldError> fieldError = Optional.ofNullable(e.getFieldError());
        String message = fieldError.isPresent() ? fieldError.get().getDefaultMessage() : "unknown error";
        if (fieldError.isPresent())
            log.debug("Validation failed: " + message);
        return new ErrorResponse("Some data is incorrect: " + e.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class, LikeNotFoundException.class,
            GenreNotFoundException.class, RatingNotFoundException.class, ReviewNotFoundException.class,
            DirectorNotFoundException.class, InvalidDataException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final RuntimeException e) {
        if (e instanceof FilmNotFoundException)
            return new ErrorResponse("Film not found");
        else if (e instanceof LikeNotFoundException)
            return new ErrorResponse("Like not found");
        else if (e instanceof RatingNotFoundException)
            return new ErrorResponse("Rating not found");
        else if (e instanceof GenreNotFoundException)
            return new ErrorResponse("Genre not found");
        else if (e instanceof ReviewNotFoundException)
            return new ErrorResponse("Review not found");
        else if (e instanceof DislikeNotFoundException)
            return new ErrorResponse("Dislike not found");
        else if (e instanceof DirectorNotFoundException)
            return new ErrorResponse("Director not found");
        return new ErrorResponse("User not found");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptions(final RuntimeException e) {
        return new ErrorResponse("Unknown error");
    }

    @ExceptionHandler({UserIsNullException.class, FilmIsNullException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final NullPointerException e) {
        if (e instanceof FilmIsNullException)
            return new ErrorResponse("Film is not specified");
        else if (e instanceof UserIsNullException)
            return new ErrorResponse("User is not specified");
        return new ErrorResponse("Entity is not specified");
    }
}
