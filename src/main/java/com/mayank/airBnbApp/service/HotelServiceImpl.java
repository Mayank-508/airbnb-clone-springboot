package com.mayank.airBnbApp.service;


import com.mayank.airBnbApp.dto.HotelDto;
import com.mayank.airBnbApp.dto.HotelInfoDto;
import com.mayank.airBnbApp.dto.RoomDto;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.Inventory;
import com.mayank.airBnbApp.entity.Room;
import com.mayank.airBnbApp.exceptions.ResourceNotFoundException;
import com.mayank.airBnbApp.repository.HotelRepository;
import com.mayank.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;


    private  final ModelMapper modelMapper;

    @Override
    public HotelDto createNewHotel(HotelDto hotelDto) {

        log.info("Creating a hotel with name: {}", hotelDto.getName());
         Hotel hotel = modelMapper.map(hotelDto,Hotel.class);
        hotel.setActive(false);
         hotel = hotelRepository.save(hotel);
         log.info("Created Hotel with the id: {}", hotel.getId());
         return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {

        log.info("Getting a Hotel with the id: {}", id);

        Hotel hotel =hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id"+id));

        return modelMapper.map(hotel, HotelDto.class);

    }




    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Getting a Hotel with the id: {}", id);

        Hotel hotel =hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id"+id));

         modelMapper.map( hotelDto, hotel);
         hotel.setId(id);

         hotel=hotelRepository.save(hotel);

         return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    @Transactional // adding Transactional where there are two database calls either all happens or not.
    public void deleteHotelById(Long id) {
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id" + id));


        for(Room room : hotel.getRooms())
        {
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.deleteById(id);

    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        Hotel hotel=hotelRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id" + id));

        hotel.setActive(true);

        hotelRepository.save(hotel);

        for(Room room: hotel.getRooms())
        {
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id" + hotelId));

        List<RoomDto> rooms= hotel.getRooms().stream()
                .map(  room -> modelMapper.map(room, RoomDto.class))
                .toList();



        return  new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);

    }


}
