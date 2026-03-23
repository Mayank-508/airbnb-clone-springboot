package com.mayank.airBnbApp.service;


import com.mayank.airBnbApp.dto.HotelDto;
import com.mayank.airBnbApp.dto.HotelSearchRequestDto;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.entity.Inventory;
import com.mayank.airBnbApp.entity.Room;
import com.mayank.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl  implements InventoryService{

    private final ModelMapper modelMapper;
 private final InventoryRepository inventoryRepository;

    @Override
    public void initializeRoomForAYear(Room room) {

        LocalDate today= LocalDate.now();
        LocalDate endDate= today.plusYears(1);

        for(; !today.isAfter(endDate); today=today.plusDays(1))
        {
            Inventory inventory= Inventory.builder()
                    .reservedCount(0)
                    .room(room)
                    .date(today)
                    .hotel(room.getHotel())
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);


        }

    }

    @Override
    public void deleteInventoryById(Long id) {

        log.info("Deleting the inventories");
        inventoryRepository.deleteById(id);
    }


    @Override
    public void deleteAllInventories( Room room) {
        LocalDate today= LocalDate.now();
        inventoryRepository.deleteByRoom( room);
    }



    @Override
    public Page<HotelDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto) {


        log.info("Searching hotels for city {} from {} to {}", hotelSearchRequestDto.getCity(), hotelSearchRequestDto.getStartDate(),hotelSearchRequestDto.getEndDate());

        Pageable pageable= PageRequest.of(hotelSearchRequestDto.getPage(), hotelSearchRequestDto.getSize());

        long dateCount= ChronoUnit.DAYS.between(hotelSearchRequestDto.getStartDate(),hotelSearchRequestDto.getEndDate())+1;

      Page<Hotel> hotelPage=  inventoryRepository.findHotelsWithAvailableInventory(
                hotelSearchRequestDto.getCity(),
                hotelSearchRequestDto.getStartDate(),
                hotelSearchRequestDto.getEndDate(),
                hotelSearchRequestDto.getRoomsCount(),
                dateCount,
                pageable
        );

      return hotelPage.map((element)-> modelMapper.map(element, HotelDto.class));
        }
    }
