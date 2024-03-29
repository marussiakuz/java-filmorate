package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotImplementedException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film add(Film film) {
        if (film.getId() == null || film.getId() == 0) {
            if (films.isEmpty())
                film.setId(1);
            else {
                int maxId = films.keySet().stream().max(Comparator.naturalOrder()).get();
                film.setId(++maxId);
            }
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void update(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void addLike(int filmId, int userId) {
        Optional<Film> filmOptional = getFilmById(filmId);
        filmOptional.ifPresent(film -> film.addLike(userId));
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        Optional<Film> filmOptional = getFilmById(filmId);
        filmOptional.ifPresent(film -> film.deleteLike(userId));
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getCountOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(int user_id, int friend_id) {
        throw new NotImplementedException();
    }

    @Override
    public boolean doesFilmExist(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public boolean doesLikeExist(int filmId, int userId) {
        if (!doesFilmExist(filmId) || getFilmById(filmId).isEmpty())
            return false;
        Film film = getFilmById(filmId).get();
        return film.getLikes().contains(userId);
    }

    @Override
    public void deleteFilmById(int filmId) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> search(String query, List<String> title) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> getMostFilmsYear(int count) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> getMostFilmsLikes(int count) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> getPopularFilmFoYearFoGenre(int year, int genreId, int count) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> getPopularFilmFoYear(int year, int count) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> getPopularFilmFoGenre(int genreId, int count) {
        throw new UnsupportedOperationException("UnsupportedOperation");
    }

    @Override
    public List<Film> getRecommendations(int userId, int bestMuchUserId) {
        throw new NotImplementedException();
    }
}

