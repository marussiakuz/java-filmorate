package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController extends AbstractController<Director> {
    private final DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director add(@Valid @RequestBody Director director) {
        return directorService.add(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable(value = "id") Integer directorId) {
        directorService.delete(directorId);
    }

    @GetMapping
    public List<Director> getAll() {
        return directorService.getAllDirectors();
    }

    @GetMapping(value = "/{id}")
    public Director getById(@PathVariable(value = "id") Integer directorId) {
        return directorService.getDirectorById(directorId);
    }
}