package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    void add(Director director);
    List<Director> getAllDirectors();
    Director getDirectorById(int id);
    void delete(int id);
    void update(Director director);
    boolean doesDirectorExist(int id);
    List<Film> getMostFilmsYear(int directorId);
    List<Film> getMostFilmsLikes(int directorId);
    List<Director> fillDirector(int filmId);
    void addDirectorToTheFilm(Film film);
    void deleteDirectorsByFilmId(int filmId);
}
