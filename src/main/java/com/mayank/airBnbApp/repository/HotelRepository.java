package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Hotel;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
}
