package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {


}
