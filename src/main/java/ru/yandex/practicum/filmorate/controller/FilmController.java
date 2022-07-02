package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/films")
public class FilmController extends AbstractController<Film> {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {    // возвращает список всех фильмов в ответ на GET запрос
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {    // добавляет  в список новый фильм в ответ на POST запрос
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        filmService.update(film);  // обновляет данные фильма в ответ на PUT запрос
        return film;
    }

    @GetMapping(value = "/{id}")
    public Film getById(@PathVariable(value = "id") Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void addLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Integer filmId, @PathVariable Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping(value = "/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", required = false) Optional<Integer> count,
                                      @RequestParam(value = "genreId", required = false) Optional<Integer> genreId,
                                      @RequestParam(value = "year", required = false) Optional<Integer> year) {

        if (genreId.isEmpty() && year.isEmpty() && count.isEmpty()) {
            return filmService.getMostPopularFilms(10);

        }
        if (genreId.isEmpty() && year.isEmpty() && count.isPresent()) {
            return filmService.getMostPopularFilms(count.get());
        } else {
            return filmService.getPopularFilmFoYearFoGenre(year, genreId, count);
        }

    }

    @DeleteMapping(value = "/{filmId}")
    public void deleteFilmById(@PathVariable(value = "filmId") Integer filmId) {
        filmService.deleteFilmByIdService(filmId);
    }

    @GetMapping(value = "/common")
    public List<Film> getCommonFilms(@RequestParam Integer userId, Integer friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping(value = "/search")
    public List<Film> search(@RequestParam(value = "query", required = false) Optional<String> query,
                             @RequestParam(value = "by", required = false) Optional<List<String>> title) {
        if (query.isEmpty() && title.isEmpty()) {
            return filmService.getMostPopularFilms(100);
        }
        return filmService.search(query, title);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortedFilmsByYearOrDirector(@PathVariable Integer directorId,
                                                     @RequestParam Optional<String> sortBy) {
        return filmService.getSortedFilmsByDirectorId(directorId, sortBy);
    }
}
