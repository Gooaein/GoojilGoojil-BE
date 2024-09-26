package com.gooaein.goojilgoojil.repository;

import com.gooaein.goojilgoojil.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}