package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

@RestController
public class FilmController extends AbstractController<Film> {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping("/films")
    public List<Film> getAll() {    // возвращает список всех фильмов в ответ на GET запрос
        return filmService.getAllFilms();
    }

    @PostMapping(value = "/films")
    public Film add(@Valid @RequestBody Film film) {    // добавляет  в список новый фильм в ответ на POST запрос
        return filmService.add(film);
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {    // обновляет данные фильма в ответ на PUT запрос
        return filmService.update(film);
    }

    @GetMapping(value = "/films/{id}")
    public Film getById(@PathVariable(value = "id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping(value = "/films/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping(value = "/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.getMostPopularFilms(count);
    }
}
