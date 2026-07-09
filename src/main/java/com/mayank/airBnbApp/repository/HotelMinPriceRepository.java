package com.mayank.airBnbApp.repository;

import com.mayank.airBnbApp.dto.HotelPriceDto;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.Optional;

public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice,Long> {




     Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);

    @Query(""" 
    SELECT new com.mayank.airBnbApp.dto.HotelPriceDto(i.hotel, AVG(i.price))
      from HotelMinPrice i
     where i.hotel.city= :cityName 
     AND i.date BETWEEN :startDate AND :endDate
     AND i.hotel.active = true
     GROUP BY i.hotel 
"""
    )
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            // @Param("cityName") means the method parameter 'city' will be passed to the query as the named parameter :cityName

            @Param("cityName") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") long dateCount,
            Pageable pageable
    );
}
