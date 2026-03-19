package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.RoomDto;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.Room;
import com.mayank.airBnbApp.exceptions.ResourceNotFoundException;
import com.mayank.airBnbApp.repository.HotelRepository;
import com.mayank.airBnbApp.repository.InventoryRepository;
import com.mayank.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
@Slf4j
public class RoomServiceImpl implements RoomService{
    private final InventoryService inventoryService;
    private final InventoryRepository inventoryRepository;
    private final RoomRepository roomRepository;
    private  final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public RoomDto createNewRoom(Long hotelId,RoomDto roomDto) {

        log.info("Attempting to create new rooms");

        Room room =modelMapper.map(roomDto, Room.class);

        Hotel hotel= hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceAccessException("No Hotel with id :" + hotelId));

        room.setHotel(hotel);

        room= roomRepository.save(room);

     if(hotel.getActive())
     {
         inventoryService.initializeRoomForAYear(room);
     }

        return  modelMapper.map(room, RoomDto.class);

    }

    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {

        log.info("Getting all rooms in a hotel");
        Hotel hotel= hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceAccessException("No Hotel with id :" + hotelId));


      List<RoomDto> roomDtos=   hotel.getRooms()
              .stream()
              .map(room -> modelMapper.map(room, RoomDto.class)).collect(Collectors.toList());


      if(roomDtos.size()==0)
      {
          throw new ResourceNotFoundException("No rooms available in Hotel with id "+ hotelId);
      }
      return roomDtos;


    }

    @Override
    public RoomDto getRoomById(Long roomId) {

        log.info("Getting a rooms by roomId "+roomId);
       Room room= roomRepository.findById(roomId).orElseThrow(
                ()-> new ResourceNotFoundException("Room does not exist with id "+roomId)
        );
        return modelMapper.map(room , RoomDto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting a room by roomId "+roomId);

        Room room= roomRepository.findById(roomId).orElseThrow(
                ()-> new ResourceNotFoundException("Room does not exist with id "+roomId)
        );

        inventoryService.deleteAllInventories(room);

        roomRepository.deleteById(roomId);




    }
}
