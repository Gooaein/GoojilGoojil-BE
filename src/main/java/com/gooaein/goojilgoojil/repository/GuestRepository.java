package com.gooaein.goojilgoojil.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gooaein.goojilgoojil.domain.Guest;
import com.gooaein.goojilgoojil.domain.Room;
import com.gooaein.goojilgoojil.dto.response.GuestResponseDto;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

	@Query("SELECT new com.gooaein.goojilgoojil.dto.response.GuestResponseDto("
		+ " g.id, g.avatarBase64) FROM Guest g WHERE g.room = :room")
	List<GuestResponseDto> findGuestsByRoom(Room room);
}
