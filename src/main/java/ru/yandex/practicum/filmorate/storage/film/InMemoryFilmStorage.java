package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void add(Film film) {
        checkId(film);
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        validateFilm(film);
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(int id) {
        if (!films.containsKey(id)) throw new FilmNotFoundException();
        return films.get(id);
    }

    private void validateFilm(Film film) {
        if (film == null || !films.containsKey(film.getId())) throw new FilmNotFoundException();
    }

    private void checkId(Film film) {
        if (film.getId() == 0) {
            if (films.isEmpty()) film.setId(1);
            else {
                int maxId = films.keySet().stream().max(Comparator.naturalOrder()).get();
                film.setId(++maxId);
            }
        }
    }
}
