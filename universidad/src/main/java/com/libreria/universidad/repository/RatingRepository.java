package com.libreria.universidad.repository;
import com.libreria.universidad.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByBookId(Long bookId);
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.book.id = :bookId")
    Double findAverageScoreByBookId(@Param("bookId") Long bookId);
}
