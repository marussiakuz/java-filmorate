package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (!userStorage.doesUserExist(userId)) throw new UserNotFoundException();
        filmStorage.getFilmById(filmId).addLike(userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        if (!userStorage.doesUserExist(userId)) throw new UserNotFoundException();
        filmStorage.getFilmById(filmId).deleteLike(userId);
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getCountOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
