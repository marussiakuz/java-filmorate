package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorStorage {

    void add(Director director);

    List<Director> getAllDirector();

    Director getDirectorById(int id);

    void delete(int id);

    void update(Director director);

    boolean doesDirectorExist(int id);


    boolean isDirectorExists(Integer id);

    List<Film> getMostFilmsYear(int count);

    List<Film> getMostFilmsLiks(int count);


}
