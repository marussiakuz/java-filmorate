package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@RestController
@Slf4j
public class FilmController extends AbstractController<Film> {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getAll() {    // возвращает список всех фильмов в ответ на GET запрос
        return filmStorage.getAllFilms();
    }

    @PostMapping(value = "/films")
    public Film add(@Valid @RequestBody Film film) {    // добавляет  в список новый фильм в ответ на POST запрос
        filmStorage.add(film);
        log.debug("new film added successfully");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {    // обновляет данные фильма в ответ на PUT запрос
        filmStorage.update(film);
        log.debug("film data has been successfully updated");
        return film;
    }

    @GetMapping(value = "/films/{id}")
    public Film getById(@PathVariable(value = "id") Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
        log.debug("like has been successfully added");
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        filmService.deleteLike(filmId, userId);
        log.debug("like has been successfully deleted");
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getMostPopularFilms(count);
    }
}
