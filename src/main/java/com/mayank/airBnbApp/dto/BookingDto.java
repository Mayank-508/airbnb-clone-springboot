package com.mayank.airBnbApp.dto;

import com.mayank.airBnbApp.entity.Guest;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.Room;
import com.mayank.airBnbApp.entity.User;
import com.mayank.airBnbApp.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {


    private Long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private BookingStatus bookingStatus;

    private Set<GuestDto> guests;



}
