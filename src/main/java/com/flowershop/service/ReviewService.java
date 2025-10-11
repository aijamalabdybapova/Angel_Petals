package com.flowershop.service;

import com.flowershop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    Review save(Review review);
    Review update(Long id, Review review);
    void delete(Long id);
    void restore(Long id);
    Review findById(Long id);
    List<Review> findAll();
    List<Review> findAllActive();
    List<Review> findAllDeleted();
    Page<Review> findAll(Pageable pageable);
    List<Review> findByBouquetId(Long bouquetId);
    List<Review> findByUserId(Long userId);
    List<Review> findApprovedReviews();
    List<Review> findPendingReviews();
    Page<Review> findApprovedByBouquetId(Long bouquetId, Pageable pageable);
    Double getAverageRatingByBouquetId(Long bouquetId);
    Long getReviewCountByBouquetId(Long bouquetId);
    void approveReview(Long reviewId);
    void rejectReview(Long reviewId);
}