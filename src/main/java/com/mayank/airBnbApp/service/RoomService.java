package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.RoomDto;

import java.util.List;

public interface RoomService {

    RoomDto createNewRoom(Long hotelId,RoomDto roomDto);

    List<RoomDto> getAllRoomsInHotel(Long HotelId);

    RoomDto getRoomById(Long roomId);

    void deleteRoomById(Long roomId);

}
