package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
}