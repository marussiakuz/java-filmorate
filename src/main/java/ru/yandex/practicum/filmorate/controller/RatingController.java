package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.rating.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public List<Rating> getAllMpa() {
        return ratingService.getAllMpa();
    }

    @GetMapping(value = "/{id}")
    public Rating getRatingById(@PathVariable(value = "id") Integer mpaId) {
        return ratingService.getRatingById(mpaId);
    }
}
