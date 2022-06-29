package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewsStorage {

    public void add(Review review);
    public void update(Review review);
    public void delete(int id);
    public Optional<Review> getReviewById(int id);
    public List<Review> getAllReviews(int count);
    public List<Review> getReviewsByFilmId(int id, int count);
    public void putLike(int reviewId, int userId);
    public void putDislike(int reviewId, int userId);
    public void deleteLike(int reviewId, int userId);
    public void deleteDislike(int reviewId, int userId);
    public boolean doesLikeExist(int reviewId, int userId);
    public boolean doesDislikeExist(int reviewId, int userId);
    public boolean doesReviewExist(int id);
}
