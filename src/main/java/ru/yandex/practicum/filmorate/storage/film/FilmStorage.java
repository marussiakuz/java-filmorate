package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {    // управление списком фильмов

    public List<Film> getAllFilms();
    public void add(Film film);
    public void update(Film film);
    public Film getFilmById(int id);
    public void addLike(int filmId, int userId);
    public void deleteLike(int filmId, int userId);
    public List<Film> getMostPopularFilms(int count);
    public boolean doesFilmExist(int id);
    public default void validateFilm(int filmId) {
        if (!doesFilmExist(filmId))
            throw new FilmNotFoundException(String.format("Film with id=%s not found", filmId));
    }
}
