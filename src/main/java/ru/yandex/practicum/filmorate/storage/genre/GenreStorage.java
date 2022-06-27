package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    public List<Genre> getAllGenres();
    public Genre getGenreById(int id);
    public boolean doesGenreExist(int id);
}
