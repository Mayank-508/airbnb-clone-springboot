package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.User;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByOwner(User user);
}
