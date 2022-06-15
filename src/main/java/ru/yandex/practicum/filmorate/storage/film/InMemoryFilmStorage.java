package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;

    public InMemoryFilmStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void add(Film film) {
        if (doesFilmExist(film.getId()))
            throw new FilmAlreadyExistException(String.format("Film with id=%s already exists", film.getId()));
        checkId(film);
        films.put(film.getId(), film);
    }

    @Override
    public void update(Film film) {
        validateFilm(film.getId());
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilmById(int id) {
        validateFilm(id);
        return films.get(id);
    }

    @Override
    public void addLike(int filmId, int userId) {
        validateFilm(filmId);

        if (!inMemoryUserStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));

        getFilmById(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        validateFilm(filmId);

        if (!inMemoryUserStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));

        getFilmById(filmId).deleteLike(userId);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getCountOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
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
