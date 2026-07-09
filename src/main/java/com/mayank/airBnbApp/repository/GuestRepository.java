package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Guest;
import com.mayank.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    List<Guest> findByUser(User user);
}