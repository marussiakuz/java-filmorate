package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Film;

@RestController
@Slf4j
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public List<Film> getFilms() {    // возвращает список всех фильмов в ответ на GET запрос
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public void addFilm(@RequestBody Film film) {    // добавляет  в список новый фильм в ответ на POST запрос
        try {
            checkTheFilmDataForCorrectness(film);
        } catch (IncorrectFilmDataException e) {
            log.debug("adding a film failed with the following error: {}", e.getMessage());
            throw new IncorrectFilmDataException(e.getMessage());
        }
        log.debug("new film added successfully");
        films.put(film.getId(), film);
    }

    @PutMapping(value = "/films")
    public void updateFilm(@RequestBody Film film) {    // обновляет данные фильма в ответ на PUT запрос
        try {
            checkTheFilmDataForCorrectness(film);
        } catch (IncorrectFilmDataException e) {
            log.debug("updating a film failed with the following error: {}", e.getMessage());
            throw new IncorrectFilmDataException(e.getMessage());
        }
        log.debug("film data has been successfully updated");
        films.put(film.getId(), film);
    }

    private void checkTheFilmDataForCorrectness (Film film) {    // проверяет данные фильма на соответствие правилам
        if (film.getName() == null || film.getName().isBlank())
            throw new FilmNameIsNotSpecifiedException("the name of the film cannot be empty");
        if (film.getDescription().isEmpty() || film.getDescription().length() > 200)
            throw new FilmDescriptionIsTooLongException("description length exceeds 200 characters");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28)))
            throw new InvalidReleaseDateException("release date earlier than December 28, 1895");
        if (film.getDuration().isNegative()) throw new NegativeDurationException("duration is negative");
    }

    @ExceptionHandler(IncorrectFilmDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handle(IncorrectFilmDataException e) {    // ловит исключения и возвращает код 400
        return new ResponseEntity<>("some film data is incorrect: " + e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    public Film getFilm(int id) {    // получить фильм из списка по id
        return films.get(id);
    }
}
