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
    public List<Film> getMostFilmsYear(int directorId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> getMostFilmsLikes(int directorId) {
        throw new UnsupportedOperationException();
    }
}