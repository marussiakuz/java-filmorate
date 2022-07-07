package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewsStorage {

    void add(Review review);
    void update(Review review);
    void delete(int id);
    Optional<Review> getReviewById(int id);
    List<Review> getAllReviews(int count);
    List<Review> getReviewsByFilmId(int id, int count);
    void putLike(int reviewId, int userId);
    void putDislike(int reviewId, int userId);
    void deleteLike(int reviewId, int userId);
    void deleteDislike(int reviewId, int userId);
    boolean doesLikeExist(int reviewId, int userId);
    boolean doesDislikeExist(int reviewId, int userId);
    boolean doesReviewExist(int id);
}
