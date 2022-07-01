package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private final DirectorStorage directorStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                        @Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film add(Film film) {
        if (film.getId() != null && filmStorage.doesFilmExist(film.getId()))
            throw new FilmAlreadyExistException(String.format("Film with id=%s already exists", film.getId()));

        filmStorage.add(film);
        log.debug(String.format("new film with id=%s added successfully", film.getId()));

        return film;
    }

    public Film update(Film film) {
        validateFilm(film.getId());

        filmStorage.update(film);
        log.debug(String.format("film data data with id=%s has been successfully updated", film.getId()));

        return film;
    }

    public Film getFilmById(int id) {
        validateFilm(id);

        Optional<Film> optionalFilm = filmStorage.getFilmById(id);
        if (optionalFilm.isEmpty())
            throw new FilmNotFoundException(String.format("Film with id=%s not found", id));

        return optionalFilm.get();
    }

    public void addLike(int filmId, int userId) {
        validateFilm(filmId);
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));

        filmStorage.addLike(filmId, userId);
        log.debug(String.format("the film with id=%s liked the user with id=%s", filmId, userId));
    }

    public void deleteLike(int filmId, int userId) {
        validateLike(filmId, userId);

        filmStorage.deleteLike(filmId, userId);
        log.debug(String.format("the film with id=%s disliked the user with id=%s", filmId, userId));
    }

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public List<Film> getSortedFilmsByDirectorId(Integer directorId, Optional<String> param) {
        if (param.isEmpty()) throw new ValidationException("Attempt to get sorted films with " +
                "empty parameter");
        if (!directorStorage.isDirectorExists(directorId)) throw new DirectorNotFoundException(
                String.format("Attempt to get sorted films with absent director id = %s", directorId));

        String sortParameter = param.get();
        switch (sortParameter) {
            case "year": {
                List<Film> films = directorStorage.getMostFilmsYear(directorId);
                return films;
            }
            case "likes": {
                List<Film> films = directorStorage.getMostFilmsLiks(directorId);
                return films;
            }
            default:
                throw new DirectorNotFoundException(String.format("Attempt to get sorted films with " +
                        "unknown parameter = %s", sortParameter));
        }
    }

    private void validateFilm(int filmId) {
        if (!filmStorage.doesFilmExist(filmId))
            throw new FilmNotFoundException(String.format("Film with id=%s not found", filmId));
    }

    private void validateLike(int filmId, int userId) {
        if (!filmStorage.doesLikeExist(filmId, userId))
            throw new LikeNotFoundException(String.format("Film with id=%s was not liked by a user with id=%s",
                    filmId, userId));
    }
}
