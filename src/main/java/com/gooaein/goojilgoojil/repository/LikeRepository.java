package com.gooaein.goojilgoojil.repository;

import com.gooaein.goojilgoojil.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndQuestionId(Long userId, String questionId);
    List<Like> findAllByUserId(Long userId);
}
