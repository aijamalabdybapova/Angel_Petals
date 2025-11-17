package com.flowershop.service.impl;

import com.flowershop.entity.Review;
import com.flowershop.repository.ReviewRepository;
import com.flowershop.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Review save(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review update(Long id, Review review) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));

        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());

        return reviewRepository.save(existingReview);
    }

    @Override
    public void delete(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        review.setDeleted(true);
        reviewRepository.save(review);
    }

    @Override
    public void restore(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        review.setDeleted(false);
        reviewRepository.save(review);
    }

    @Override
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
    }

    @Override
    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    @Override
    public List<Review> findAllActive() {
        return reviewRepository.findAllActive();
    }

    @Override
    public List<Review> findAllDeleted() {
        return reviewRepository.findAllDeleted();
    }

    @Override
    public Page<Review> findAll(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    @Override
    public List<Review> findByBouquetId(Long bouquetId) {
        return reviewRepository.findByBouquetId(bouquetId);
    }

    @Override
    public List<Review> findByUserId(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public List<Review> findApprovedReviews() {
        return reviewRepository.findByIsApprovedTrue();
    }

    @Override
    public List<Review> findPendingReviews() {
        return reviewRepository.findByIsApprovedFalse();
    }

    @Override
    public Page<Review> findApprovedByBouquetId(Long bouquetId, Pageable pageable) {
        return reviewRepository.findApprovedByBouquetId(bouquetId, pageable);
    }

    @Override
    public Double getAverageRatingByBouquetId(Long bouquetId) {
        return reviewRepository.findAverageRatingByBouquetId(bouquetId);
    }

    @Override
    public Long getReviewCountByBouquetId(Long bouquetId) {
        return reviewRepository.countApprovedByBouquetId(bouquetId);
    }

    @Override
    public void approveReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        review.setIsApproved(true);
        reviewRepository.save(review);
    }

    @Override
    public void rejectReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Отзыв не найден"));
        review.setIsApproved(false);
        reviewRepository.save(review);
    }


}