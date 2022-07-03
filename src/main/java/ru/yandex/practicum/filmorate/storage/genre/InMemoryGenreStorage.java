package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public class InMemoryGenreStorage implements GenreStorage {
    @Override
    public List<Genre> getAllGenres() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Genre getGenreById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doesGenreExist(int id) {
        throw new UnsupportedOperationException();
    }
}
