package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class FilmManagerService extends ManagerService<Film> {    // управление списком фильмов
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> get() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void add(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Film get(int id) {
        return films.get(id);
    }
}
