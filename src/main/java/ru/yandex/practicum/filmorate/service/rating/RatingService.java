package ru.yandex.practicum.filmorate.service.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Slf4j
@Service
public class RatingService {

    private final RatingStorage ratingStorage;

    public RatingService(@Qualifier("ratingDbStorage") RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public List<Rating> getAllMpa() {
        return ratingStorage.getAllMpa();
    }

    public Rating getRatingById(int id) {
        return ratingStorage.getRatingById(id);
    }
}
