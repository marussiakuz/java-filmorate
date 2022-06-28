package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {    // управление списком фильмов

    List<Film> getAllFilms();

    void add(Film film);

    void update(Film film);

    Optional<Film> getFilmById(int id);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> getMostPopularFilms(int count);

    List<Film> getCommonFilms(int user_id, int friend_id);

    boolean doesFilmExist(int id);

    boolean doesLikeExist(int filmId, int userId);
}
