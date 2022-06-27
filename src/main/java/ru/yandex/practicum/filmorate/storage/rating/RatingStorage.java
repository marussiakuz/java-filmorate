package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingStorage {

    public List<Rating> getAllMpa();
    public Rating getRatingById(int id);
    public boolean doesRatingExist(int id);
}
