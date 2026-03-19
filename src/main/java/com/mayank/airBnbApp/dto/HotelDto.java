package com.mayank.airBnbApp.dto;

import com.mayank.airBnbApp.entity.HotelContactInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Data
public class HotelDto {

    private Long id;
    @Size(min = 4, message = "Hotel name can't be lesser than 4 characters")
    private String name;

    private String city;

    private String[] photos;

    @Column(columnDefinition = "TEXT[]")
    private String[] amenities;

    private HotelContactInfo contactInfo;

    private Boolean active;
}
