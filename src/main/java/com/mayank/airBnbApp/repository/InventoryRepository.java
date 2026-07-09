package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.dto.UpdateInventoryRequestDto;
import com.mayank.airBnbApp.entity.Booking;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.Inventory;
import com.mayank.airBnbApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    void deleteByRoom(Room room);

    // Hotel is denormalized(made redundant) in Inventory to avoid joins,
// enabling direct hotel search without extra queries.


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



    List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);



    @Query("""
                SELECT i
                FROM Inventory i
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> findAndLockReservedInventory(@Param("roomId") Long roomId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate,
                                                 @Param("numberOfRooms") int numberOfRooms);


    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount - :numberOfRooms,
                    i.bookedCount = i.bookedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount) >= :numberOfRooms
                  AND i.reservedCount >= :numberOfRooms
                  AND i.closed = false
            """)
    int confirmBooking(@Param("roomId") Long roomId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate,
                        @Param("numberOfRooms") int numberOfRooms);


    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.reservedCount = i.reservedCount + :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND (i.totalCount - i.bookedCount - i.reservedCount) >= :numberOfRooms
                  AND i.closed = false
            """)
    void initBooking(@Param("roomId") Long roomId,
                     @Param("startDate") LocalDate startDate,
                     @Param("endDate") LocalDate endDate,
                     @Param("numberOfRooms") int numberOfRooms);

    @Modifying
    @Query("""
                UPDATE Inventory i
                SET i.bookedCount = i.bookedCount - :numberOfRooms
                WHERE i.room.id = :roomId
                  AND i.date BETWEEN :startDate AND :endDate
                  AND  i.bookedCount >= :numberOfRooms
                  AND i.closed = false
            """)
    int cancelBooking(@Param("roomId") Long roomId,
                       @Param("startDate") LocalDate startDate,
                       @Param("endDate") LocalDate endDate,
                       @Param("numberOfRooms") int numberOfRooms);


    List<Inventory> findByRoomOrderByDate(Room room);

    @Query("""
   select i from  Inventory i
   where 
   i.room.id= :roomId
   AND i.date Between :startDate AND :endDate
""")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Inventory> getInventoryAndLockBeforeUpdate(@Param("roomId") Long roomId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate
    );

    @Modifying
    @Query("""
   Update Inventory i 
   set i.surgeFactor = :surgeFactor,
   i.closed = :closed
   where 
   i.room.id= :roomId
   AND i.date Between :startDate AND :endDate
""")
    void updateInventory(@Param("roomId") Long roomId,
                           @Param("startDate") LocalDate startDate,
                           @Param("endDate") LocalDate endDate,
                           @Param("surgeFactor") BigDecimal surgeFactor,
                           @Param("closed") Boolean closed
                           );
}
