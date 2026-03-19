package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface RoomRepository extends JpaRepository<Room, Long> {
}
