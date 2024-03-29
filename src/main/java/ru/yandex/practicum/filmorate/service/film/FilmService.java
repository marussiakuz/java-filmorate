package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.InvalidDataException;
import ru.yandex.practicum.filmorate.exceptions.entityAlreadyExcistsExceptions.FilmAlreadyExistsException;
import ru.yandex.practicum.filmorate.exceptions.entityNotFoundExceptions.*;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.EventType;

import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.ValidationException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final RatingStorage ratingStorage;
    private final EventStorage eventStorage;
    private final DirectorStorage directorStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("directorDbStorage") DirectorStorage directorStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("ratingDbStorage") RatingStorage ratingStorage,
                       @Qualifier("eventDbStorage") EventStorage eventStorage,
                       @Qualifier("genreDbStorage") GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
        this.ratingStorage = ratingStorage;
        this.eventStorage = eventStorage;
        this.genreStorage = genreStorage;
    }

    public List<Film> getAllFilms() {
        List<Film> films = filmStorage.getAllFilms();

        fillFilmsWithGenres(films);
        fillFilmsWithDirectors(films);

        return films;
    }

    public Film add(Film film) {
        if (film.getId() != null && filmStorage.doesFilmExist(film.getId()))
            throw new FilmAlreadyExistsException(String.format("Film with id=%s already exists", film.getId()));

        filmStorage.add(film);
        log.debug(String.format("new film with id=%s added successfully", film.getId()));

        film.setMpa(ratingStorage.getRatingById(film.getMpa().getId()));
        if (film.getGenres() != null) genreStorage.addGenresToTheFilm(film);
        if (film.getDirectors() != null && !film.getDirectors().isEmpty())
            directorStorage.addDirectorToTheFilm(film);

        return film;
    }

    public Film update(Film film) {
        validateFilm(film.getId());

        filmStorage.update(film);
        updateDirectorsAndGenres(film);
        log.debug(String.format("film data with id=%s has been successfully updated", film.getId()));

        return film;
    }

    public void deleteFilmById(int filmId) {
        validateFilm(filmId);

        filmStorage.deleteFilmById(filmId);
        log.debug(String.format("the film with id=%s was deleted", filmId));
    }

    public Film getFilmById(int id) {
        validateFilm(id);

        Optional<Film> optionalFilm = filmStorage.getFilmById(id);
        if (optionalFilm.isPresent()) {
            List<Genre> genres = genreStorage.fillGenre(id);
            optionalFilm.get().setGenres(genres.isEmpty() ? null : genres);

            List<Director> directors = directorStorage.fillDirector(id);
            optionalFilm.get().setDirectors(directors);
        }
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

        eventStorage.addAddEvent(userId, filmId, EventType.LIKE);
        log.debug("the add like event was completed successfully");
    }

    public void deleteLike(int filmId, int userId) {
        validateLike(filmId, userId);

        filmStorage.deleteLike(filmId, userId);
        log.debug(String.format("the film with id=%s disliked the user with id=%s", filmId, userId));

        eventStorage.addRemoveEvent(userId, filmId, EventType.LIKE);
        log.debug("the delete like event was completed successfully");
    }

    public List<Film> getSortedFilmsByDirectorId(Integer directorId, Optional<String> param) {
        if (param.isEmpty())
            throw new ValidationException("Attempt to get sorted films with empty parameter");
        if (directorStorage.doesDirectorExist(directorId))
            throw new DirectorNotFoundException(String.format("Attempt to get sorted films with absent director id = %s",
                    directorId));

        List<Film> sortedFilms;
        String sortParameter = param.get();
        switch (sortParameter) {
            case "year": {
                sortedFilms = filmStorage.getMostFilmsYear(directorId);
                break;
            }
            case "likes": {
                sortedFilms = filmStorage.getMostFilmsLikes(directorId);
                break;
            }
            default:
                throw new DirectorNotFoundException(String.format("Attempt to get sorted films with " +
                        "unknown parameter = %s", sortParameter));
        }

        fillFilmsWithDirectors(sortedFilms);
        fillFilmsWithGenres(sortedFilms);
        sortedFilms.stream().filter(film -> film.getGenres().size() == 0).forEach(film -> film.setGenres(null));

        return sortedFilms;
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
        if (!userStorage.doesUserExist(friendId))
            throw new UserNotFoundException(String.format("User with id=%s not found", friendId));
        List<Film> common = filmStorage.getCommonFilms(userId, friendId);

        common.stream()
                .filter(film -> !genreStorage.fillGenre(film.getId()).isEmpty())
                .forEach(film -> film.setGenres(genreStorage.fillGenre(film.getId())));
        return common;
    }

    public List<Film> search(String query, List<String> title) {
        if (query != null && title != null) {
            List<Film> searchList = filmStorage.search(query, title);

            fillFilmsWithGenres(searchList);
            fillFilmsWithDirectors(searchList);
            searchList.stream().filter(film -> film.getGenres().size() == 0).forEach(film -> film.setGenres(null));

            return searchList;
        }

        return filmStorage.getMostPopularFilms(100);
    }

    public List<Film> getMostPopularFilms(Integer year, Integer genreId, int count) {
        if (genreId != null || year != null) {
            return genreId != null && year != null ? getPopularFilmFoYearFoGenre(year, genreId, count) : genreId != null ?
                    getPopularFilmFoGenre(genreId, count) : getPopularFilmFoYear(year, count);
        }

        List<Film> popular = filmStorage.getMostPopularFilms(count);
        fillFilmsWithGenres(popular);
        fillFilmsWithDirectors(popular);

        return popular;
    }

    private List<Film> getPopularFilmFoYearFoGenre(int year, int genreId, int count) {
        validateYear(year);
        validateGenre(genreId);

        List<Film> foYearFoGenre = filmStorage.getPopularFilmFoYearFoGenre(year, genreId, count);
        fillFilmsWithGenres(foYearFoGenre);

        return foYearFoGenre;
    }

    private List<Film> getPopularFilmFoYear(int year, int count) {
        validateYear(year);

        List<Film> foYear = filmStorage.getPopularFilmFoYear(year, count);
        fillFilmsWithGenres(foYear);

        return filmStorage.getPopularFilmFoYear(year, count);
    }

    private List<Film> getPopularFilmFoGenre(int genreId, int count) {
        validateGenre(genreId);

        List<Film> foGenre = filmStorage.getPopularFilmFoGenre(genreId, count);
        fillFilmsWithGenres(foGenre);

        return filmStorage.getPopularFilmFoGenre(genreId, count);
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

    private void validateGenre(int genreId) {
        if (genreId <= 0 || genreId > 6)
            throw new GenreNotFoundException(genreId < 0 ? "negative param" : "there is no such genre");
    }

    private void validateYear(int year) {
        if (year < 1895)
            throw new InvalidDataException(year < 0 ? "negative param" : "Release date may not be earlier than " +
                    "28.12.1895");
    }

    private void updateDirectorsAndGenres(Film film) {
        directorStorage.deleteDirectorsByFilmId(film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty())
            directorStorage.addDirectorToTheFilm(film);

        if (film.getGenres() != null) {
            genreStorage.deleteGenresByFilmId(film.getId());
            genreStorage.addGenresToTheFilm(film);
        }
    }

    private void fillFilmsWithGenres(List<Film> films) {
        films.forEach(film -> film.setGenres(genreStorage.fillGenre(film.getId())));
    }

    private void fillFilmsWithDirectors(List<Film> films) {
        films.forEach(film -> film.setDirectors(directorStorage.fillDirector(film.getId())));
    }
}
