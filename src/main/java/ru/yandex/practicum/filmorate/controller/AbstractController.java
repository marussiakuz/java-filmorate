package ru.yandex.practicum.filmorate.controller;

import java.util.List;

public abstract class AbstractController<T> {
    public abstract List<T> getAll();

    public abstract T add(T type);

    public abstract T update(T type);

    public abstract T getById(Integer id);
}
