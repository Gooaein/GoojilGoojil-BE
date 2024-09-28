package com.gooaein.goojilgoojil.repository;

import com.gooaein.goojilgoojil.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {
    Optional<Room> findByUrl(String url);
    List<Room> findAllByUserId(Long userId);
}
