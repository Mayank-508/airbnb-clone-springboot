package com.mayank.airBnbApp.controller;


import com.mayank.airBnbApp.dto.RoomDto;
import com.mayank.airBnbApp.repository.RoomRepository;
import com.mayank.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
public class RoomController {

    private final RoomService roomService;


    @PostMapping
    public ResponseEntity<RoomDto> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDto roomDto)
    {
      RoomDto room=  roomService.createNewRoom(hotelId, roomDto);
        return new ResponseEntity<>(room, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RoomDto>> getAllRoomsInHotel(@PathVariable Long hotelId)
    {
        List<RoomDto> rooms=  roomService.getAllRoomsInHotel(hotelId);

          return new ResponseEntity<>(rooms, HttpStatus.OK);
    }


    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoomInHotel(@PathVariable Long hotelId, @PathVariable Long roomId)
    {
        RoomDto room=  roomService.getRoomById(roomId);

        return new ResponseEntity<>(room, HttpStatus.OK);
    }

@DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long hotelId, @PathVariable Long roomId)
    {
         roomService.deleteRoomById(roomId);

        return ResponseEntity.noContent().build();
    }
}
