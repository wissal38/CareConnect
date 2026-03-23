package com.kindconnect.repository;

import com.kindconnect.model.Review;
import com.kindconnect.model.UserAuthenticated;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @EntityGraph(attributePaths = {"user", "publication"})
    List<Review> findByUser(UserAuthenticated user);
    
    @EntityGraph(attributePaths = {"user", "publication"})
    List<Review> findByPublicationId(Long publicationId);
    
    @EntityGraph(attributePaths = {"user", "publication"})
    List<Review> findByRatingGreaterThanEqual(int minRating);
    
    long countByUser(UserAuthenticated user);
    
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.publication.id = :publicationId")
    double calculateAverageRatingByPublicationId(@Param("publicationId") Long publicationId);
}
