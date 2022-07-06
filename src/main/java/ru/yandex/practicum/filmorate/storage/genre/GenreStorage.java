package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> getAllGenres();
    Genre getGenreById(int id);
    boolean doesGenreExist(int id);
    List<Genre> fillGenre(int filmId);
    void addGenresToTheFilm(Film film);
    void deleteGenresByFilmId(int filmId);
}
