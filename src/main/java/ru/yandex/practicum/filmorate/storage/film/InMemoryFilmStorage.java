package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
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
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public boolean doesFilmExist(int filmId) {
        return films.containsKey(filmId);
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
