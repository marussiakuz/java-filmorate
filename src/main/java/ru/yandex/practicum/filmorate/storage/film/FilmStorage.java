package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {    // управление списком фильмов

    public List<Film> getAllFilms();
    public void add(Film film);
    public void update(Film film);
    public Film getFilmById(int id);
    public boolean doesFilmExist(int id);
}
