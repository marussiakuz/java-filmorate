package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

public abstract class AbstractController<T> {

    public abstract List<T> get();
    public abstract T add(T type);
    public abstract void update(T type);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public abstract ResponseEntity<String> handle(MethodArgumentNotValidException e);
}
