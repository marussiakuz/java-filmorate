package ru.yandex.practicum.filmorate.service.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director add(Director director) {
        if (director.getId() == null)
            throw new DirectorNotFoundException(String.format("Director with id=%s already exists", director.getId()));

        directorStorage.add(director);
        log.debug(String.format("new director with id=%s added successfully", director.getId()));

        return director;
    }

    public void delete(Integer directorId) {
        directorStorage.delete(directorId);
        log.debug(String.format("The director with id=%s has been deleted", directorId));
    }

    public Director update(Director director) {
        if (director.getId() != null && directorStorage.doesDirectorExist(director.getId()))
        directorStorage.update(director);
        log.debug(String.format("director with id=%s have been successfully updated", director.getId()));

        return director;
    }

    public List<Director> getAllDirector() {
        return directorStorage.getAllDirector();
    }

    public Director getDirectorById(int id) {
        validate(id);
        return directorStorage.getDirectorById(id);
    }

    private void validate(int id) {
        if (!directorStorage.doesDirectorExist(id))
            throw new DirectorNotFoundException(String.format("Director with id=%s not found", id));
    }
}