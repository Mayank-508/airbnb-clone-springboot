package com.mayank.airBnbApp.service;


import com.mayank.airBnbApp.dto.*;
import com.mayank.airBnbApp.entity.*;
import com.mayank.airBnbApp.repository.HotelMinPriceRepository;
import com.mayank.airBnbApp.repository.InventoryRepository;
import com.mayank.airBnbApp.repository.RoomRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mayank.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl  implements InventoryService{
    private final RoomRepository roomRepository;

    private final ModelMapper modelMapper;
 private final InventoryRepository inventoryRepository;
 private final HotelMinPriceRepository hotelMinPriceRepository;

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
    public Page<HotelPriceDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto) {


        log.info("Searching hotels for city {} from {} to {}", hotelSearchRequestDto.getCity(), hotelSearchRequestDto.getStartDate(),hotelSearchRequestDto.getEndDate());

        Pageable pageable= PageRequest.of(hotelSearchRequestDto.getPage(), hotelSearchRequestDto.getSize());

        long dateCount= ChronoUnit.DAYS.between(hotelSearchRequestDto.getStartDate(),hotelSearchRequestDto.getEndDate())+1;

        // business Logic - for  within 90 days

      Page<HotelPriceDto> hotelPage=  hotelMinPriceRepository.findHotelsWithAvailableInventory(
                hotelSearchRequestDto.getCity(),
                hotelSearchRequestDto.getStartDate(),
                hotelSearchRequestDto.getEndDate(),
                hotelSearchRequestDto.getRoomsCount(),
                dateCount,
                  pageable
        );

      return hotelPage;
        }

    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {

        log.info("Getting all inventories by room with id: {}", roomId);

        Room room = roomRepository.findById(roomId).orElseThrow(()->new ResourceAccessException("Room does not exist with id "+roomId));

        User user= getCurrentUser();

        if(! user.equals(room.getHotel().getOwner()))throw new AccessDeniedException("You are not the owner of the hotel");

        List<Inventory> inventories=inventoryRepository.findByRoomOrderByDate(room);

       return inventories.stream().map(inventory-> modelMapper.map(inventory, InventoryDto.class)).collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {

        log.info("Updating all inventory with roomId: {} between date range {} and {}", roomId, updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());
        Room room = roomRepository.findById(roomId).orElseThrow(()->new ResourceAccessException("Room does not exist with id "+roomId));

        User user= getCurrentUser();

        if(! user.equals(room.getHotel().getOwner()))throw new AccessDeniedException("You are not the owner of the hotel");

     List<Inventory> inventories= inventoryRepository.getInventoryAndLockBeforeUpdate(roomId, updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(roomId, updateInventoryRequestDto.getStartDate(), updateInventoryRequestDto.getEndDate(), updateInventoryRequestDto.getSurgefactor(), updateInventoryRequestDto.getClosed());

    }


}
