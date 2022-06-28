package ru.yandex.practicum.filmorate.service.review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewsStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReviewService {

    private final ReviewsStorage reviewsStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewService(@Qualifier("reviewsDbStorage") ReviewsStorage reviewsStorage,
                         @Qualifier("filmDbStorage") FilmStorage filmStorage,
                         @Qualifier("userDbStorage") UserStorage userStorage) {
        this.reviewsStorage = reviewsStorage;
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Review addReview(Review review) {
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());

        if (review.getId() != null && reviewsStorage.doesReviewExist(review.getId()))
            throw new ReviewAlreadyExistsException(String.format("Review with id=%s already exists", review.getId()));

        reviewsStorage.add(review);
        log.debug(String.format("new review with id=%s added successfully", review.getId()));

        return review;
    }

    public Review update(Review review) {
        validateReview(review.getId());
        validateUser(review.getUserId());
        validateFilm(review.getFilmId());

        reviewsStorage.update(review);
        log.debug(String.format("review data with id=%s has been successfully updated", review.getId()));

        return review;
    }

    public void delete(int id) {
        validateReview(id);
        reviewsStorage.delete(id);
    }

    public Review getById(int id) {
        validateReview(id);

        Optional<Review> optionalReview = reviewsStorage.getReviewById(id);
        if (optionalReview.isEmpty())
            throw new ReviewNotFoundException(String.format("Review with id=%s not found", id));

        return optionalReview.get();
    }

    public List<Review> getAllReviews(int count) {
        return reviewsStorage.getAllReviews(count);
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        validateFilm(filmId);

        return reviewsStorage.getReviewsByFilmId(filmId, count);
    }

    public void putLike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);

        reviewsStorage.putLike(reviewId, userId);
    }

    public void putDislike(int reviewId, int userId) {
        validateReview(reviewId);
        validateUser(userId);

        reviewsStorage.putDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        validateLike(reviewId, userId);
        reviewsStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        validateDislike(reviewId, userId);
        reviewsStorage.deleteDislike(reviewId, userId);
    }

    private void validateReview(int id) {
        if (!reviewsStorage.doesReviewExist(id))
            throw new ReviewNotFoundException(String.format("Review with id=%s not found", id));
    }

    private void validateUser(int userId) {
        if (!userStorage.doesUserExist(userId))
            throw new UserNotFoundException(String.format("User with id=%s not found", userId));
    }

    private void validateLike(int reviewId, int userId) {
        if (!reviewsStorage.doesLikeExist(reviewId, userId))
            throw new LikeNotFoundException(String.format("Review with id=%s was not liked by a user with id=%s",
                    reviewId, userId));
    }

    private void validateDislike(int reviewId, int userId) {
        if (!reviewsStorage.doesDislikeExist(reviewId, userId))
            throw new DislikeNotFoundException(String.format("Review with id=%s was not disliked by a user with id=%s",
                    reviewId, userId));
    }

    private void validateFilm(int filmId) {
        if (!filmStorage.doesFilmExist(filmId))
            throw new FilmNotFoundException(String.format("Film with id=%s not found", filmId));
    }
}
