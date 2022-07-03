package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {

    List<Rating> getAllMpa();
    Rating getRatingById(int id);
    boolean doesRatingExist(int id);
}
