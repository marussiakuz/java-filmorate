package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {    // управление списком фильмов

    public List<Film> getAllFilms();
    public void add(Film film);
    public void update(Film film);
    public Optional<Film> getFilmById(int id);
    public void addLike(int filmId, int userId);
    public void deleteLike(int filmId, int userId);
    public List<Film> getMostPopularFilms(int count);

    List<Film> getCommonFilms(int user_id, int friend_id);

    public boolean doesFilmExist(int id);
    public boolean doesLikeExist(int filmId, int userId);

    List<Film> getRecommendations(int userId);
}
