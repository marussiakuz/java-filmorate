package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;


import java.util.List;

public interface DirectorStorage {

    public void add(Director director);

    public List<Director> getAllDirector();

    public Director getDirectorById(int id);

    public void delete(int id);

    public void update(Director director);

    public boolean doesDirectorExist(int id);


    public  boolean isDirectorExists(Integer id);

    List<Film> getMostFilmsYear(int count);

    List<Film> getMostFilmsLiks(int count);


}
