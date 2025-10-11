package com.flowershop.repository;

import com.flowershop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByBouquetId(Long bouquetId);
    List<Review> findByUserId(Long userId);
    List<Review> findByIsApprovedTrue();
    List<Review> findByIsApprovedFalse();

    @Query("SELECT r FROM Review r WHERE r.deleted = false")
    List<Review> findAllActive();

    @Query("SELECT r FROM Review r WHERE r.deleted = true")
    List<Review> findAllDeleted();

    @Query("SELECT r FROM Review r WHERE r.bouquet.id = :bouquetId AND r.isApproved = true AND r.deleted = false")
    Page<Review> findApprovedByBouquetId(@Param("bouquetId") Long bouquetId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.bouquet.id = :bouquetId AND r.isApproved = true AND r.deleted = false")
    Double findAverageRatingByBouquetId(@Param("bouquetId") Long bouquetId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.bouquet.id = :bouquetId AND r.isApproved = true AND r.deleted = false")
    Long countApprovedByBouquetId(@Param("bouquetId") Long bouquetId);
}