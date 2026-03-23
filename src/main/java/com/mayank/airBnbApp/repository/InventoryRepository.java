package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.entity.Booking;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.Inventory;
import com.mayank.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);

    // Hotel is denormalized(made redundant) in Inventory to avoid joins,
// enabling direct hotel search without extra queries.
    @Query(""" 
    SELECT DISTINCT i.hotel from Inventory i 
     where i.city= :cityName 
     AND i.date BETWEEN :startDate AND :endDate
     AND i.closed = false
     AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
     GROUP BY i.hotel, i.room HAVING COUNT(i.date)= :dateCount
"""
    )
    Page<Hotel> findHotelsWithAvailableInventory(
            // @Param("cityName") means the method parameter 'city' will be passed to the query as the named parameter :cityName

            @Param("cityName") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") long dateCount,
            Pageable pageable
    );


    @Query("""
      SELECT i FROM  Inventory i where 
      i.room.id= :roomId AND
      i.date BETWEEN :startDate AND :endDate AND
      i.closed= false  AND
      (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
      
""")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockAvailableInventory(
            @Param("roomId") Long roomId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount

    );



}
