package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmManagerService;

@RestController
@Slf4j
public class FilmController extends AbstractController<Film> {
    private final FilmManagerService filmManagerService;

    @Autowired
    public FilmController(FilmManagerService filmManagerService) {
        this.filmManagerService = filmManagerService;
    }

    @GetMapping("/films")
    public List<Film> get() {    // возвращает список всех фильмов в ответ на GET запрос
        return filmManagerService.get();
    }

    @PostMapping(value = "/films")
    public Film add(@Valid @RequestBody Film film) {    // добавляет  в список новый фильм в ответ на POST запрос
        log.debug("new film added successfully");
        filmManagerService.add(film);
        return film;
    }

    @PutMapping(value = "/films")
    public void update(@Valid @RequestBody Film film) {    // обновляет данные фильма в ответ на PUT запрос
        log.debug("film data has been successfully updated");
        filmManagerService.update(film);
    }

    @Override
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handle(MethodArgumentNotValidException e) {
        Optional<FieldError> fieldError = Optional.ofNullable(e.getFieldError());
        String message = fieldError.isPresent()? fieldError.get().getDefaultMessage() : "unknown error";
        if (fieldError.isPresent()) log.debug("Film validation failed: " + message);
        return new ResponseEntity<>("Some film data is incorrect: " + e.getFieldError().getDefaultMessage(),
                HttpStatus.BAD_REQUEST);
    }
}
