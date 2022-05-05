package ru.yandex.practicum.filmorate.exceptions;

public class FilmNameIsNotSpecifiedException extends IncorrectFilmDataException {
    public FilmNameIsNotSpecifiedException(String message) {
        super(message);
    }
}
