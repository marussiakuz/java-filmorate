package ru.yandex.practicum.filmorate.exceptions;

public class FilmDescriptionIsTooLongException extends IncorrectFilmDataException {
    public FilmDescriptionIsTooLongException(String message) {
        super(message);
    }
}
