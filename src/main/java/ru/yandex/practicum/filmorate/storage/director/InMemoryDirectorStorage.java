package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public class InMemoryDirectorStorage implements DirectorStorage {
    @Override
    public void add(Director director) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Director> getAllDirectors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Director getDirectorById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(Director director) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doesDirectorExist(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Director> fillDirector(int filmId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDirectorToTheFilm(Film film) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteDirectorsByFilmId(int filmId) {
        throw new UnsupportedOperationException();
    }
}
