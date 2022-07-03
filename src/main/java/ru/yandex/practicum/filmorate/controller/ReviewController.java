package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review add(@Valid @RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteLike(@PathVariable(value = "id") Integer reviewId) {
        reviewService.delete(reviewId);
    }

    @GetMapping(value = "/{id}")
    public Review getById(@PathVariable(value = "id") Integer reviewId) {
        return reviewService.getById(reviewId);
    }

    @GetMapping
    public List<Review> getReviewsByFilmId(@RequestParam(required = false) Integer filmId,
                                           @RequestParam(defaultValue = "10", required = false) Integer count) {
        if (filmId == null) return reviewService.getAllReviews(count);
        return reviewService.getReviewsByFilmId(filmId, count);
    }

    @PutMapping(value = "/{id}/like/{userId}")
    public void putLike(@PathVariable(value = "id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.putLike(reviewId, userId);
    }

    @PutMapping(value = "/{id}/dislike/{userId}")
    public void putDislike(@PathVariable(value = "id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.putDislike(reviewId, userId);
    }

    @DeleteMapping(value = "/{id}/like/{userId}")
    public void deleteLike(@PathVariable(value = "id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping(value = "/{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable(value = "id") Integer reviewId, @PathVariable Integer userId) {
        reviewService.deleteDislike(reviewId, userId);
    }
}
