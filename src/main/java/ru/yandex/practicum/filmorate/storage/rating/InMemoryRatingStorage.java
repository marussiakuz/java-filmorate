package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public class InMemoryRatingStorage implements RatingStorage {
    @Override
    public List<Rating> getAllMpa() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rating getRatingById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doesRatingExist(int id) {
        throw new UnsupportedOperationException();
    }
}
