package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryReviewsStorage")
public class InMemoryReviewsStorage implements ReviewsStorage {

    private final Map<Integer, Review> reviews = new HashMap<>();
    private final Map<Integer, Set<Integer>> likes = new HashMap<>();
    private final Map<Integer, Set<Integer>> dislikes = new HashMap<>();

    @Override
    public void add(Review review) {
        if (review.getId() == null || review.getId() == 0) {
            if (reviews.isEmpty()) review.setId(1);
            else {
                int maxId = reviews.keySet().stream().max(Comparator.naturalOrder()).get();
                review.setId(++maxId);
            }
        }
        reviews.put(review.getId(), review);
    }

    @Override
    public void update(Review review) {
        reviews.put(review.getId(), review);
    }

    @Override
    public void delete(int id) {
        reviews.remove(id);
        likes.remove(id);
        dislikes.remove(id);
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return reviews.values().stream()
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> getReviewsByFilmId(int id, int count) {
        return reviews.values().stream()
                .filter(review -> review.getFilmId() == id)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Review> getReviewById(int id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public void putLike(int reviewId, int userId) {
        Set<Integer> reviewLikes = likes.get(reviewId);
        reviewLikes.add(userId);

        likes.put(reviewId, reviewLikes);

        Review review = reviews.get(reviewId);
        review.setUseful(review.getUseful() + 1);
    }

    @Override
    public void putDislike(int reviewId, int userId) {
        Set<Integer> reviewDislikes = dislikes.get(reviewId);
        reviewDislikes.add(userId);

        likes.put(reviewId, reviewDislikes);

        Review review = reviews.get(reviewId);
        review.setUseful(review.getUseful() - 1);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        Set<Integer> reviewLikes = likes.get(reviewId);
        reviewLikes.remove(userId);

        Review review = reviews.get(reviewId);
        review.setUseful(review.getUseful() - 1);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        Set<Integer> reviewDislikes = dislikes.get(reviewId);
        reviewDislikes.remove(userId);

        Review review = reviews.get(reviewId);
        review.setUseful(review.getUseful() + 1);
    }

    @Override
    public boolean doesLikeExist(int reviewId, int userId) {
        return likes.containsKey(reviewId) && likes.get(reviewId).contains(userId);
    }

    @Override
    public boolean doesDislikeExist(int reviewId, int userId) {
        return dislikes.containsKey(reviewId) && dislikes.get(reviewId).contains(userId);
    }

    @Override
    public boolean doesReviewExist(int id) {
        return reviews.containsKey(id);
    }
}
