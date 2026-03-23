package com.mayank.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class HotelInfoDto {
    private  HotelDto hotelDto;
    private List<RoomDto> roomDto;
}
