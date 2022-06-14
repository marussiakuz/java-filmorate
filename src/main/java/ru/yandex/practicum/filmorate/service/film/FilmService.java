package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmInMemoryService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    public FilmInMemoryService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film add(Film film) {
        if (filmStorage.doesFilmExist(film.getId()))
            throw new FilmAlreadyExistException(String.format("Film with id=%s already exists", film.getId()));
        filmStorage.add(film);
        log.debug("new film added successfully");
        return film;
    }

    public Film update(Film film) {
        validateFilm(film.getId());
        filmStorage.update(film);
        log.debug(String.format("film data data with id=%s has been successfully updated", film.getId()));
        return film;
    }

    public Optional<Film> getFilmById(int id) {
        validateFilm(id);
        return filmStorage.getFilmById(id);
    }

    public void addLike(Integer filmId, Integer userId) {
        validateFilm(filmId);
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
        filmStorage.getFilmById(filmId).get().addLike(userId);
        log.debug(String.format("the film with id=%s liked the user with id=%s", filmId, userId));
    }

    public void deleteLike(Integer filmId, Integer userId) {
        validateFilm(filmId);
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
        filmStorage.getFilmById(filmId).get().deleteLike(userId);
        log.debug(String.format("the film with id=%s disliked the user with id=%s", filmId, userId));
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getCountOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilm(int filmId) {
        if (!filmStorage.doesFilmExist(filmId))
            throw new FilmNotFoundException(String.format("Film with id=%s not found", filmId));
    }
}
