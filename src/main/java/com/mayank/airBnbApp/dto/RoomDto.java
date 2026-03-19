package com.mayank.airBnbApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mayank.airBnbApp.entity.Hotel;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RoomDto {

    private Long id;

    @JsonIgnore   // Used for internal logic/mapping only; excluded from API request/response using @JsonIgnore
    private Hotel hotel;

    private String type;

    private BigDecimal basePrice;

    private String[] photos;

    private String[] amenities;

    private Integer totalCount;

    private Integer capacity;


}
