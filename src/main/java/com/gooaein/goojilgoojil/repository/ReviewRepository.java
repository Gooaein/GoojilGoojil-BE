package com.gooaein.goojilgoojil.repository;

import com.gooaein.goojilgoojil.domain.Review;
import com.gooaein.goojilgoojil.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByRoom(Room room);
}
