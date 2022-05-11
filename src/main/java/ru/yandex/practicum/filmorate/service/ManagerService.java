package ru.yandex.practicum.filmorate.service;

import java.util.List;

public abstract class ManagerService<T> {    // абстрактный класс для управления добавлением/обновлением/получением элемента

    public abstract List<T> get();
    public abstract void add(T type);
    public abstract void update(T type);
    public abstract T get(int id);
}
